package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface DomainTablesRepository {
    suspend fun getDomainTables(): AppResult<Unit, DataError>
}
