package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.TenantReadView
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface TenantDatabaseDataSource {
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<TenantReadView, InfraError>

    suspend fun getNameById(id: Long): AppResult<String?, InfraError>

    suspend fun getAllLeadsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantReadView>, InfraError>

    suspend fun getAllTenantsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantReadView>, InfraError>

    suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<TenantReadView>, InfraError>

    suspend fun getAllTagNames(): AppResult<List<String>, InfraError>

    suspend fun getAllCityNames(): AppResult<List<String>, InfraError>

    suspend fun getAllContactOrigins(): AppResult<List<String>, InfraError>

    fun watchById(id: Long): AppResult<Flow<TenantReadView?>, InfraError>

    fun watchAll(
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<TenantReadView>>, InfraError>
}
