package com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.dtos.LeadTenantReadModel
import kotlinx.collections.immutable.ImmutableList

interface LeadTenantQueryRepository {
    suspend fun getById(leadTenantId: Long): AppResult<LeadTenantReadModel, ApplicationError>

    suspend fun getAllTagNames(): AppResult<ImmutableList<String>, ApplicationError>

    suspend fun getAllCityNames(): AppResult<ImmutableList<String>, ApplicationError>

    suspend fun getAllContactOrigins(): AppResult<ImmutableList<String>, ApplicationError>
}
