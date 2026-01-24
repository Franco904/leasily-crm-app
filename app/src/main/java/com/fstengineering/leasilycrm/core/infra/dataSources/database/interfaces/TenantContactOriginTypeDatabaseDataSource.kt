package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface TenantContactOriginTypeDatabaseDataSource {
    suspend fun insertOrUpdate(tenantContactOriginType: TenantContactOriginTypeEntity)

    suspend fun getAll(): AppResult<List<TenantContactOriginTypeEntity>, DataError>
}
