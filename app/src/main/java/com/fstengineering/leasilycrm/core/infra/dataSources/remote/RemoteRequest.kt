package com.fstengineering.leasilycrm.core.infra.dataSources.remote

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.utils.toRemoteDataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.utils.io.CancellationException

suspend fun HttpClient.requestBackend(
    url: String,
    method: HttpMethod,
    body: Any? = null,
): AppResult<HttpResponse, DataError> {
    try {
        val response = request(url) {
            this.method = method
            if (body != null) setBody(body)
        }

        if (response.status.value in 200..299) {
            return AppResult.Success(data = response)
        }

        // Handle backend errors
        return AppResult.Error(error = response.status.toRemoteDataSourceError())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        if (BuildConfig.DEBUG) e.printStackTrace()

        // Handle HTTP client errors
        return AppResult.Error(error = e.toRemoteDataSourceError())
    }
}
