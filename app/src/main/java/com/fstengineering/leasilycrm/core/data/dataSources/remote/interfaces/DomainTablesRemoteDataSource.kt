package com.fstengineering.leasilycrm.core.data.dataSources.remote.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.remote.responseDtos.domainTables.GetDomainTablesDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface DomainTablesRemoteDataSource {
    suspend fun getDomainTables(): AppResult<GetDomainTablesDto, DataError>
}
