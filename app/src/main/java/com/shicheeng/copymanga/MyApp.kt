package com.shicheeng.copymanga

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import com.shicheeng.copymanga.database.MangaHistoryDataBase
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.util.AppSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class MyApp : Application() {

    companion object {
        lateinit var appContext: Context
    }

    val scope = CoroutineScope(SupervisorJob())
    private val dataBase by lazy { MangaHistoryDataBase.getDataBase(this) }
    val repo by lazy { MangaHistoryRepository(dataBase.historyDao()) }
    val appPreference by lazy {
        AppSetting.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        appContext = applicationContext
    }


}