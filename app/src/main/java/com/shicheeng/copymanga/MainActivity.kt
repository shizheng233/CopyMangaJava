package com.shicheeng.copymanga

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.data.VersionUnit
import com.shicheeng.copymanga.ui.screen.MainComposeNavigation
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.ui.screen.topics.TopicViewModel
import com.shicheeng.copymanga.ui.theme.CopyMangaTheme
import com.shicheeng.copymanga.util.FileCacheUtils
import com.shicheeng.copymanga.util.collectRepeatLifecycle
import com.shicheeng.copymanga.util.observe
import com.shicheeng.copymanga.viewmodel.MainViewModel
import com.shicheeng.copymanga.viewmodel.MangaInfoViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import retrofit2.HttpException
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppAttachCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var settingPref: SettingPref


    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model: MainViewModel by viewModels()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestNotificationsPermission()
        setContent {
            val navController = rememberMaterialMotionNavController()
            CopyMangaTheme {
                Scaffold {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CompositionLocalProvider(
                            LocalMainBottomNavigationPadding provides it.calculateBottomPadding(),
                            LocalSettingPreference provides settingPref,
                        ) {
                            MainComposeNavigation(navController = navController)
                        }
                    }
                }
            }
        }

        if (!settingPref.pauseUpdateDetector.value) {
            model.updateData.collectRepeatLifecycle(this) {
                onUpdateAttach(it)
            }
        }

        model.loginInfoStatus.observe(this) {
            if (it != null) {
                if ((it is HttpException) && (it.code() == 401)) {
                    Snackbar.make(
                        window.decorView,
                        getString(R.string.login_expired),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        window.decorView,
                        getString(R.string.login_failure),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    private fun requestNotificationsPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    /**
     * 更新弹窗。由于一般的更新内容为简体中文，故不做i18n处理。
     * @param versionUnit 版本更新单位，为空则表示没有更新。
     *
     * @author ShihCheeng and refer to Kotatsu.
     */
    private fun onUpdateAttach(versionUnit: VersionUnit?) {
        if (versionUnit == null) {
            return
        }
        val message = buildSpannedString {
            append("<b>版本：</b>")
            append(versionUnit.versionName)
            appendLine()
            appendLine(versionUnit.description)
            appendLine("<b>大小：</b>" + FileCacheUtils.getFormatSize(versionUnit.apkSize.toDouble()))
        }
        val dialog = MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        ).apply {
            setTitle(R.string.new_version)
            setMessage(message)
            setIcon(R.drawable.baseline_security_update_24)
        }
        dialog.setPositiveButton(R.string.update) { dialogInterface: DialogInterface, _: Int ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionUnit.apkUrl))
            startActivity(intent)
            dialogInterface.dismiss()
        }
        dialog.setNegativeButton(android.R.string.cancel) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        dialog.setNeutralButton(R.string.website_look) { dialogInterface: DialogInterface, _: Int ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionUnit.htmlUrl))
            startActivity(intent)
            dialogInterface.dismiss()
        }
        dialog.show()
    }


    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelAssistedFactoryProvider {
        fun infoViewModelFactory(): MangaInfoViewModel.InfoViewModelFactory
        fun topicDetailViewModelFactory(): TopicViewModel.Factory
    }

}

val LocalMainBottomNavigationPadding = staticCompositionLocalOf { 0.dp }
val LocalSettingPreference = staticCompositionLocalOf<SettingPref> {
    error("NO LOCAL SETTING PROVIDE")
}