package com.shicheeng.copymanga.modula

import android.content.Context
import coil.ImageLoader
import com.shicheeng.copymanga.resposity.LoginTokenRepository
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.KeyWordSwap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
import javax.inject.Singleton

private val queryUrlRegex = "/api/v3/comic2/.*/query".toRegex()
private val comic2UrlRegex = "/api/v3/comic/.*/chapter2/.*".toRegex()

@Module
@InstallIn(SingletonComponent::class)
object OkhttpProvider {

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
    ): ImageLoader {
        return ImageLoader.Builder(context).okHttpClient(okHttpClient).build()
    }

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
        .add("version", "2023.08.14")
        .add("referer", "https://www.copymanga.site/")
        .add(KeyWordSwap.USER_AGENT_WORD, KeyWordSwap.FAKE_USER_AGENT)
        .build()

    @Provides
    @Singleton
    fun provideOkhttp(
        headers: Headers,
        loginTokenRepository: LoginTokenRepository,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val oldRequest = chain.request()
                val token = loginTokenRepository.token
                // TODO 能否使用更简单的方式来进行判断
                val headersNew = if (
                    (oldRequest.url.toUrl().path == "/api/v3/member/browse/comics" ||
                            oldRequest.url.toUrl().path == "/api/v3/member/collect/comics" ||
                            oldRequest.url.toUrl().path == "/api/v3/member/update/info" ||
                            oldRequest.url.toUrl().path.matches(queryUrlRegex) ||
                            oldRequest.url.toUrl().path.matches(comic2UrlRegex) ||
                            oldRequest.url.toUrl().path == "/api/v3/member/info" ||
                            oldRequest.url.toUrl().path == "/api/v3/member/collect/comic" ||
                            oldRequest.url.toUrl().path == "/api/v3/member/comment")
                    && token != null && !loginTokenRepository.isExpired
                ) {
                    headers.newBuilder()
                        .add("Authorization", "Token $token")
                        .build()
                } else {
                    headers
                }

                val newRequest = oldRequest.newBuilder().headers(headersNew)
                chain.proceed(newRequest.build())
            }
            .build()
    }

}