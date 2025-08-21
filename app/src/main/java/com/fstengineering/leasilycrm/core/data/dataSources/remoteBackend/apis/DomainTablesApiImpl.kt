package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis

import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.HttpClientManager
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.DomainTablesApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables.GetDomainTablesDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class DomainTablesApiImpl(
    private val client: HttpClientManager,
) : DomainTablesApi {
    override suspend fun getDomainTables(): AppResult<GetDomainTablesDto, DataSourceError.RemoteBackendError> {
        return client.request(
            url = ROUTE,
            method = HttpMethod.Get,
        ).fold(
            onError = { AppResult.Error(error = it) },
            onSuccess = { response -> AppResult.Success(data = response.body()) },
        )
    }

    companion object {
        private const val ROUTE = "/domain-tables"
    }
}
