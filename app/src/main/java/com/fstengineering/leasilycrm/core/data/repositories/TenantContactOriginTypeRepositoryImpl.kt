package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantContactOriginTypeDao
import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType
import com.fstengineering.leasilycrm.core.domain.repositories.TenantContactOriginTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class TenantContactOriginTypeRepositoryImpl(
    private val dao: TenantContactOriginTypeDao,
) : TenantContactOriginTypeRepository {
    override suspend fun getAll(): AppResult<ImmutableList<TenantContactOriginType>, DataSourceError> {
        val tenantContactOriginTypes = dao.getAll()
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = tenantContactOriginTypes)
    }
}
