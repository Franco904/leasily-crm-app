package com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantRawReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers.toRawReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LeadTenantQueryRepositoryImpl(
    private val tenantDatabaseDataSource: TenantDatabaseDataSource,
) : LeadTenantQueryRepository {
    override fun watchById(id: Long): Flow<AppResult<LeadTenantRawReadModel?, ApplicationError>> =
        flow {
            val tenantFlow = tenantDatabaseDataSource.watchById(id = id)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map { view ->
                    val raw = view?.toRawReadModel()
                        ?: return@map AppResult.Success(data = null)

                    AppResult.Success<LeadTenantRawReadModel?, ApplicationError>(data = raw)
                }
                .distinctUntilChanged()

            emitAll(tenantFlow)
        }
}
