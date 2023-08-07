package com.shicheeng.copymanga.modula

import android.content.SharedPreferences
import com.shicheeng.copymanga.domin.CopyMangaApi
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
    fun headersProvide(
        settingPref: SettingPref,
    ): Headers = Headers.Builder()
        .add(
            "region",
            if (settingPref.useForeignApi) "1" else "0"
        )
        .add("webp", "0")
        .add("platform", "1")
        .add("version", "2023.04.14")
        .add("referer", "https://www.copymanga.site/")
        .add(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
        .build()

    @Provides
    @Singleton
    fun provideOkhttp(
        headers: Headers,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val oldRequest = chain.request()
                val newRequest = oldRequest.newBuilder().headers(headers)
                chain.proceed(newRequest.build())
            }
            .build()
    }

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


