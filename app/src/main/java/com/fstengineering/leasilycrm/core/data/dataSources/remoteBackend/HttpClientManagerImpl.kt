package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend

import android.util.Log
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.exceptions.toRemoteBackendError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.utils.io.CancellationException

class HttpClientManagerImpl(
    private val client: HttpClient,
) : HttpClientManager {
    override suspend fun request(
        url: String,
        method: HttpMethod,
        body: Any?,
    ): AppResult<HttpResponse, DataSourceError.RemoteBackendError> {
        try {
            val response = client.request(url) {
                this.method = method
                if (body != null) setBody(body)
            }

            if (response.status.value in 200..299) {
                return AppResult.Success(data = response)
            }

            // Handle standard HTTP errors
            return AppResult.Error(error = response.status.toRemoteBackendError())
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "request error - ${e.message}")
                e.printStackTrace()
            }

            // Handle Network & Serialization Errors
            return AppResult.Error(error = e.toRemoteBackendError())
        }
    }

    companion object {
        private const val TAG = "HttpClientManagerImpl"
    }
}
