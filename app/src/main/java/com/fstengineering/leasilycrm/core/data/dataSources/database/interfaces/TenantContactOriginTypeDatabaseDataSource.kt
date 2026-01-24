package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface TenantContactOriginTypeDatabaseDataSource {
    suspend fun insertOrUpdate(tenantContactOriginType: TenantContactOriginTypeEntity)

    suspend fun getAll(): AppResult<List<TenantContactOriginTypeEntity>, DataError>
}
