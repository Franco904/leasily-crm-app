package com.fstengineering.leasilycrm.core.infra.dataSources.remote

import com.fstengineering.leasilycrm.core.infra.constants.SIGN_IN_ROUTE
import com.fstengineering.leasilycrm.core.infra.constants.SIGN_OUT_ROUTE
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.http.HttpStatusCode

private val rejectedSessionStatuses = setOf(HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden)

private val sessionlessRoutes = setOf(SIGN_IN_ROUTE, SIGN_OUT_ROUTE)

/**
 * Único ponto que reage à recusa do token de sessão pelo back-end (401/403): encerra a sessão.
 * Login e sign-out ficam de fora — no login ainda não há sessão, e no sign-out o encerramento
 * já está em curso.
 */
fun HttpClientConfig<*>.validateSession(onSessionExpired: suspend () -> Unit) {
    HttpResponseValidator {
        validateResponse { response ->
            val isSessionRejected = response.status in rejectedSessionStatuses
            val path = response.call.request.url.encodedPath
            val isSessionlessRoute = sessionlessRoutes.any { path.endsWith(it) }

            if (isSessionRejected && !isSessionlessRoute) onSessionExpired()
        }
    }
}
