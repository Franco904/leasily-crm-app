package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.TenantCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface TenantDatabaseDataSource {
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<TenantCompound?, InfraError>

    suspend fun getAllLeadsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, InfraError>

    suspend fun getAllTenantsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, InfraError>

    suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<TenantCompound>, InfraError>

    suspend fun getNameById(id: Long): AppResult<String, InfraError>

    suspend fun getAllTagNames(): AppResult<List<String>, InfraError>

    suspend fun getAllCityNames(): AppResult<List<String>, InfraError>

    fun watchById(id: Long): AppResult<Flow<TenantCompound?>, InfraError>

    fun watchAll(
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<TenantCompound>>, InfraError>

    suspend fun countTenantsWithName(
        tenantName: String,
        currentStoredTenantName: String?,
    ): AppResult<Int, InfraError>
}
