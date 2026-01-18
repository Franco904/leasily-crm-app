package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantContactOriginTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.TenantContactOriginTypeDatabaseDataSource
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
