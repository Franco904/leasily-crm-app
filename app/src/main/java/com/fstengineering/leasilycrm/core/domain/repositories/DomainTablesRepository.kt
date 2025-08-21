package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface DomainTablesRepository {
    suspend fun getDomainTables(): AppResult<Unit, DataSourceError>
}
