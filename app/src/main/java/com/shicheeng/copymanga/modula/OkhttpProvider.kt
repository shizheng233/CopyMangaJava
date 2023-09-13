package com.shicheeng.copymanga.modula

import com.shicheeng.copymanga.resposity.LoginTokenRepository
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.KeyWordSwap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
import javax.inject.Singleton

private val _queryUrlRegex = "/api/v3/comic2/.*/query".toRegex()
private val _comic2UrlRegex = "/api/v3/comic/.*/chapter2/.*".toRegex()

@Module
@InstallIn(SingletonComponent::class)
object OkhttpProvider {

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
                val headersNew = if (
                    (oldRequest.url.toUrl().path == "/api/v3/member/browse/comics" ||
                            oldRequest.url.toUrl().path == "/api/v3/member/collect/comics" ||
                            oldRequest.url.toUrl().path == "/api/v3/member/update/info" ||
                            oldRequest.url.toUrl().path.matches(_queryUrlRegex) ||
                            oldRequest.url.toUrl().path.matches(_comic2UrlRegex) ||
                            oldRequest.url.toUrl().path == "/api/v3/member/info" ||
                            oldRequest.url.toUrl().path == "/api/v3/member/collect/comic")
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