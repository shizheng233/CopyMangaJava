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
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.data.VersionUnit
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.ui.screen.MainComposeNavigation
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.ui.theme.CopyMangaTheme
import com.shicheeng.copymanga.util.FileCacheUtils
import com.shicheeng.copymanga.util.collectRepeatLifecycle
import com.shicheeng.copymanga.viewmodel.MainViewModel
import com.shicheeng.copymanga.viewmodel.MangaInfoViewModel
import com.shicheeng.copymanga.viewmodel.PersonalViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
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
                            LocalSettingPreference provides settingPref
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
        fun personalViewModelProvider(): PersonalViewModel.Factory
        fun mangaInfoRepository(): MangaInfoRepository
        fun infoViewModelFactory(): MangaInfoViewModel.InfoViewModelFactory
        fun historyRepositoryProvider(): MangaHistoryRepository
    }

}

val LocalMainBottomNavigationPadding = staticCompositionLocalOf { 0.dp }
val LocalSettingPreference = staticCompositionLocalOf<SettingPref> {
    error("NO LOCAL SETTING PROVIDE")
}