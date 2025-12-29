package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod

interface HttpClientAdapter {
    suspend fun request(
        url: String,
        method: HttpMethod,
        body: Any? = null,
    ): AppResult<HttpResponse, DataError>
}
