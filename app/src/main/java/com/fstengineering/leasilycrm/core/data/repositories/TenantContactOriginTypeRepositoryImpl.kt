package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantContactOriginTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType
import com.fstengineering.leasilycrm.core.domain.repositories.TenantContactOriginTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class TenantContactOriginTypeRepositoryImpl(
    private val databaseDataSource: TenantContactOriginTypeDatabaseDataSource,
) : TenantContactOriginTypeRepository {
    override suspend fun getAll(): AppResult<ImmutableList<TenantContactOriginType>, DataError> {
        val tenantContactOriginTypes = databaseDataSource.getAll()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = tenantContactOriginTypes)
    }
}
