package com.shicheeng.copymanga.modula

import android.content.SharedPreferences
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.resposity.LoginRepository
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.KeyWordSwap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CopyMangaApiModula {



    @Provides
    @Singleton
    fun provideCopyMangaApi(
        retrofit: Retrofit,
    ): CopyMangaApi {
        return retrofit.create(CopyMangaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        sharedPreferences: SharedPreferences,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val headerTheKey = "https://api."
        val apiName = sharedPreferences
            .getString("key_api_header_select", "copymanga.net")
            ?: "copymanga.net"
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("$headerTheKey$apiName")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }


}


