package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.data.dataSources.remote.DomainTablesRemoteDataSourceImpl
import com.fstengineering.leasilycrm.core.data.dataSources.remote.UserRemoteDataSourceImpl
import com.fstengineering.leasilycrm.core.data.dataSources.remote.cookies.UserSessionCookieStorage
import com.fstengineering.leasilycrm.core.data.dataSources.remote.interfaces.DomainTablesRemoteDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.remote.interfaces.UserRemoteDataSource
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

val remoteDataSourceModule = module {
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
                storage = UserSessionCookieStorage(
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

    single<DomainTablesRemoteDataSource> { DomainTablesRemoteDataSourceImpl(client = get()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(client = get()) }
}
