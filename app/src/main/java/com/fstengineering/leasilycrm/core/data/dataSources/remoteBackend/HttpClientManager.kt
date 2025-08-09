package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod

interface HttpClientManager {
    suspend fun request(
        url: String,
        method: HttpMethod,
        body: Any? = null,
    ): AppResult<HttpResponse, DataSourceError.RemoteBackendError>
}
