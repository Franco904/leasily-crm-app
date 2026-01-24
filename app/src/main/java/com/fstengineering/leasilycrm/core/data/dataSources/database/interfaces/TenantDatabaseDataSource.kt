package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.TenantCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface TenantDatabaseDataSource {
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<TenantCompound?, DataError>

    suspend fun getAllLeadsMatching(
        userId: Long,
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, DataError>

    suspend fun getAllTenantsMatching(
        userId: Long,
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, DataError>

    suspend fun getAllWithoutUpcomingStaysMatching(
        userId: Long,
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<TenantCompound>, DataError>

    suspend fun getNameById(id: Long): AppResult<String, DataError>

    suspend fun getAllTagNames(userId: Long): AppResult<List<String>, DataError>

    suspend fun getAllCityNames(userId: Long): AppResult<List<String>, DataError>

    fun watchById(id: Long): AppResult<Flow<TenantCompound?>, DataError>

    fun watchAll(
        userId: Long,
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<TenantCompound>>, DataError>

    suspend fun countTenantsWithName(
        userId: Long,
        tenantName: String,
        currentStoredTenantName: String?,
    ): AppResult<Int, DataError>
}
