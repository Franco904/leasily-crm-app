package com.fstengineering.leasilycrm.core.infra.api.cookies

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.flow.firstOrNull

class UserSessionCookieStorage(
    private val encryptedPreferencesStorage: LocalStorage<EncryptedPreferences>,
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
