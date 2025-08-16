package com.fstengineering.leasilycrm.core.data.dataSources.cookieStorage

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.domain.utils.fold
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.flow.first

class AppCookieStorage(
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
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
                onSuccess = { prefsFlow -> prefsFlow.first().sessionCookie }
            )

        return if (sessionCookie == null) emptyList() else listOf(sessionCookie)
    }

    override fun close() {}
}
