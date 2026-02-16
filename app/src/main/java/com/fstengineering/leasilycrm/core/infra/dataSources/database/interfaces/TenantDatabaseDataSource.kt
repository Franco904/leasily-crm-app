package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.TenantCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface TenantDatabaseDataSource {
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<TenantCompound?, DataError>

    suspend fun getAllLeadsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, DataError>

    suspend fun getAllTenantsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, DataError>

    suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<TenantCompound>, DataError>

    suspend fun getNameById(id: Long): AppResult<String, DataError>

    suspend fun getAllTagNames(): AppResult<List<String>, DataError>

    suspend fun getAllCityNames(): AppResult<List<String>, DataError>

    fun watchById(id: Long): AppResult<Flow<TenantCompound?>, DataError>

    fun watchAll(
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<TenantCompound>>, DataError>

    suspend fun countTenantsWithName(
        tenantName: String,
        currentStoredTenantName: String?,
    ): AppResult<Int, DataError>
}
