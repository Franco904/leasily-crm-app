package com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.domainTables.GetDomainTablesDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface DomainTablesRemoteDataSource {
    suspend fun getDomainTables(): AppResult<GetDomainTablesDto, DataError>
}
