package com.fstengineering.leasilycrm.core.data.dataSources.remote

import com.fstengineering.leasilycrm.core.data.constants.DOMAIN_TABLES_ROUTE
import com.fstengineering.leasilycrm.core.data.dataSources.remote.interfaces.DomainTablesRemoteDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.remote.responseDtos.domainTables.GetDomainTablesDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class DomainTablesRemoteDataSourceImpl(
    private val client: HttpClient,
) : DomainTablesRemoteDataSource {
    override suspend fun getDomainTables(): AppResult<GetDomainTablesDto, DataError> {
        return client.requestBackend(
            url = DOMAIN_TABLES_ROUTE,
            method = HttpMethod.Get,
        ).fold(
            onError = { AppResult.Error(error = it) },
            onSuccess = { response -> AppResult.Success(data = response.body()) },
        )
    }
}
