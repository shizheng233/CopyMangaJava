package com.shicheeng.copymanga

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.databinding.ActivityMainHostBinding
import com.shicheeng.copymanga.viewmodel.MainViewModel

class MainActivity : AppAttachCompatActivity() {

    private lateinit var binding: ActivityMainHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val model: MainViewModel by viewModels()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        windowsPaddingUp(binding.root, binding.hostMainToolbarLayout)
        setSupportActionBar(binding.hostMainToolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(navController.graph)

        binding.hostMainToolbar.setupWithNavController(navController, appBarConfig)
        requestNotificationsPermission()
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
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }


}