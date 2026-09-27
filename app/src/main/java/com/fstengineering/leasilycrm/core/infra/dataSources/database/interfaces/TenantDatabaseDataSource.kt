package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.LeadTenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import kotlinx.coroutines.flow.Flow

interface TenantDatabaseDataSource {
    suspend fun insertOrUpdate(tenant: LeadTenantEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<LeadTenantReadView, ApplicationError>

    suspend fun getNameById(id: Long): AppResult<String, ApplicationError>

    suspend fun getAllLeads(
        matching: String?,
        order: Int,
    ): AppResult<List<LeadTenantReadView>, ApplicationError>

    suspend fun getAllTenants(
        matching: String?,
        order: Int,
    ): AppResult<List<LeadTenantReadView>, ApplicationError>

    suspend fun getAllWithoutUpcomingStays(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<LeadTenantReadView>, ApplicationError>

    suspend fun getAllTagNames(): AppResult<List<String>, ApplicationError>

    suspend fun getAllCityNames(): AppResult<List<String>, ApplicationError>

    suspend fun getAllContactOrigins(): AppResult<List<String>, ApplicationError>

    fun watchById(id: Long): AppResult<Flow<LeadTenantReadView?>, ApplicationError>

    fun watchAll(
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<LeadTenantReadView>>, ApplicationError>
}
