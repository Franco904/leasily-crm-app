package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.application.services.UserSessionService
import com.fstengineering.leasilycrm.core.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.UserRemoteDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.classifyNetworkFailures
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.cookies.UserSessionCookieStorage
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.validateSession
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

            classifyNetworkFailures(connectivityAdapter = get())

            // Resolvido sob demanda: UserSessionService depende do HttpClient (sign-out remoto).
            validateSession(onSessionExpired = { get<UserSessionService>().onSessionExpired() })

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.BODY
                }
            }
        }
    }

    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(client = get()) }
}
