package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.TenantContactOriginTypeDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantContactOriginTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class TenantContactOriginTypeDatabaseDataSourceImpl(
    private val dao: TenantContactOriginTypeDao,
) : TenantContactOriginTypeDatabaseDataSource {
    override suspend fun insertOrUpdate(
        tenantContactOriginType: TenantContactOriginTypeEntity,
    ) =
        dao.insertOrUpdate(tenantContactOriginType)

    override suspend fun getAll(): AppResult<List<TenantContactOriginTypeEntity>, DataError> =
        onDatabaseSafe { dao.getAll() }
}
