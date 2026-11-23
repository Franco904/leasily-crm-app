package com.fstengineering.leasilycrm.core.infra.dataSources.remote

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.onBoundary
import com.fstengineering.leasilycrm.core.infra.utils.toRemoteDataSourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod

suspend fun HttpClient.requestBackend(
    url: String,
    method: HttpMethod,
    body: Any? = null,
): AppResult<HttpResponse> {
    val response = onBoundary(toError = { it.toRemoteDataSourceError() }) {
        request(url) {
            this.method = method
            if (body != null) setBody(body)
        }
    }.let { result ->
        when (result) {
            is AppResult.Error -> return result
            is AppResult.Success -> result.data
        }
    }

    if (response.status.value in 200..299) {
        return AppResult.Success(data = response)
    }

    return AppResult.Error(error = response.status.toRemoteDataSourceError())
}
