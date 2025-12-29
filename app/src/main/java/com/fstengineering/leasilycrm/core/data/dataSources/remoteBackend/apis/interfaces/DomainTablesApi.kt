package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables.GetDomainTablesDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface DomainTablesApi {
    suspend fun getDomainTables(): AppResult<GetDomainTablesDto, DataError>
}
