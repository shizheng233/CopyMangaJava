package com.shicheeng.copymanga

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.material.color.DynamicColors
import com.shicheeng.copymanga.server.work.DetectMangaUpdateWork
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.ThemeMode
import com.shicheeng.copymanga.util.setSystemNightMode
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    companion object {
        lateinit var appContext: Context
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    private lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var settingPref: SettingPref


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(NotificationManager::class.java)
        DynamicColors.applyToActivitiesIfAvailable(this)
        appContext = applicationContext
        bindNotification()
        val themeMode = ThemeMode.valueOf(settingPref.appThemeMode)
        setSystemNightMode(themeMode)
    }

    private fun bindNotification() {
        val name = getString(R.string.update_manga)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(
            DetectMangaUpdateWork.DETECT_UPDATE_CHANELLE,
            name,
            importance
        )
        notificationManager.createNotificationChannel(mChannel)
    }


}