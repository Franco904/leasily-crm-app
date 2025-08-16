package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.data.dataSources.cookieStorage.AppCookieStorage
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.HttpClientManager
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.HttpClientManagerImpl
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.UserApiImpl
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val remoteBackendModule = module {
    single<HttpClient> {
        HttpClient(OkHttp) {
            engine {
                config {
                    readTimeout(30, TimeUnit.SECONDS)
                    connectTimeout(30, TimeUnit.SECONDS)
                }
            }

            defaultRequest {
                url(BuildConfig.REMOTE_API_URL)
                contentType(ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(HttpCookies) {
                storage = AppCookieStorage(
                    encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
                )
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.BODY
                }
            }
        }
    }

    single<HttpClientManager> { HttpClientManagerImpl(client = get()) }

    single<UserApi> { UserApiImpl(client = get()) }
}
