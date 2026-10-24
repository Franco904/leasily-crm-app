package com.fstengineering.leasilycrm.core.infra.dataSources.remote

import com.fstengineering.leasilycrm.core.infra.adapters.ConnectivityAdapter
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.io.IOException

class NoInternetException(cause: Throwable) : IOException(cause)

/**
 * Classifica as falhas de rede da requisição: viram falta de internet quando o aparelho está sem
 * conexão; com conexão, seguem como estão.
 */
fun HttpClientConfig<*>.classifyNetworkFailures(connectivityAdapter: ConnectivityAdapter) {
    val plugin = createClientPlugin("NetworkFailureClassification") {
        on(Send) { request ->
            try {
                proceed(request)
            } catch (exception: IOException) {
                if (!connectivityAdapter.isDeviceConnectedToInternet()) throw NoInternetException(exception)
                throw exception
            }
        }
    }
    install(plugin)
}
