package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.utils.toRemoteBackendDataError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.utils.io.CancellationException

class HttpClientAdapterImpl(
    private val client: HttpClient,
) : HttpClientAdapter {
    override suspend fun request(
        url: String,
        method: HttpMethod,
        body: Any?,
    ): AppResult<HttpResponse, DataError> {
        try {
            val response = client.request(url) {
                this.method = method
                if (body != null) setBody(body)
            }

            if (response.status.value in 200..299) {
                return AppResult.Success(data = response)
            }

            // Handle standard HTTP errors
            return AppResult.Error(error = response.status.toRemoteBackendDataError())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            // Handle Network & Serialization Errors
            return AppResult.Error(error = e.toRemoteBackendDataError())
        }
    }
}
