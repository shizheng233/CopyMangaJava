package com.shicheeng.copymanga.modula

import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
        settingPref: SettingPref,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val headerTheKey = "https://api."
        val apiName = settingPref.apiSelected
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("$headerTheKey$apiName")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }


}


