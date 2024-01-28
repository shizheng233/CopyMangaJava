package com.shicheeng.copymanga

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.data.VersionUnit
import com.shicheeng.copymanga.ui.screen.MainComposeNavigation
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.ui.theme.CopyMangaTheme
import com.shicheeng.copymanga.util.FileCacheUtils
import com.shicheeng.copymanga.util.collectRepeatLifecycle
import com.shicheeng.copymanga.viewmodel.RootViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppAttachCompatActivity() {

    @Inject
    lateinit var settingPref: SettingPref

    private val mainViewModel by viewModels<RootViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestNotificationsPermission()
        setContent {
            CopyMangaTheme {
                CompositionLocalProvider(
                    LocalSettingPreference provides settingPref,
                ) {
                    MainComposeNavigation()
                }
            }
        }

        if (!settingPref.pauseUpdateDetector.value) {
            mainViewModel.updateData.collectRepeatLifecycle(this) {
                onUpdateAttach(it)
            }
        }

    }

    private fun requestNotificationsPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                /*context=*/this,
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
            appendLine("<b>大小：</b>" + FileCacheUtils.getFormatSize(versionUnit.apkSize.toDouble()))
            appendLine("<b>类型：</b>" + versionUnit.versionId.type)
            appendLine()
            appendLine(versionUnit.description)
        }
        val dialog = MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        ).apply {
            setTitle(R.string.new_version)
            setMessage(Html.fromHtml(message.toString(), Html.FROM_HTML_MODE_COMPACT))
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


}

val LocalSettingPreference = staticCompositionLocalOf<SettingPref> {
    error("NO LOCAL SETTING PROVIDE")
}