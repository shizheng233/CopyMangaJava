package com.shicheeng.copymanga

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.data.VersionUnit
import com.shicheeng.copymanga.databinding.ActivityMainHostBinding
import com.shicheeng.copymanga.json.UpdateMetaDataJson
import com.shicheeng.copymanga.util.FileCacheUtils
import com.shicheeng.copymanga.viewmodel.MainViewModel
import com.shicheeng.copymanga.viewmodel.MainViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppAttachCompatActivity() {

    private lateinit var binding: ActivityMainHostBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val model: MainViewModel by viewModels {
            MainViewModelFactory(UpdateMetaDataJson())
        }
        sharedPreferences = (application as MyApp).appPreference
        val isDisableUpdateDetect = sharedPreferences.getBoolean("disable_update", false)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        windowsPaddingUp(binding.root, binding.hostMainToolbarLayout)
        setSupportActionBar(binding.hostMainToolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(navController.graph)

        binding.hostMainToolbar.setupWithNavController(navController, appBarConfig)
        requestNotificationsPermission()

        if (!isDisableUpdateDetect) {
            lifecycleScope.launchWhenCreated {
                model.updateData.collectLatest {
                    onUpdateAttach(it)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(navController.graph)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
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
        val dialog = MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        ).apply {
            setTitle(R.string.new_version)
            setMessage(
                "${versionUnit.versionName}\n${versionUnit.description}\n${
                    FileCacheUtils.getFormatSize(versionUnit.apkSize.toDouble())
                }"
            )

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