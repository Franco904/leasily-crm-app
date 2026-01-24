package com.fstengineering.leasilycrm.core.infra.dataSources.remote.cookies

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.domain.utils.fold
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.flow.firstOrNull

class UserSessionCookieStorage(
    private val encryptedPreferencesStorage: PreferencesDataSource<EncryptedPreferences>,
) : CookiesStorage {
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        if (cookie.name == BuildConfig.SESSION_COOKIE_NAME) {
            encryptedPreferencesStorage.savePreferences { prefs ->
                prefs.copy(sessionCookie = cookie)
            }
        }
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val sessionCookie = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { null },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.sessionCookie }
            )

        return if (sessionCookie == null) emptyList() else listOf(sessionCookie)
    }

    override fun close() {}
}