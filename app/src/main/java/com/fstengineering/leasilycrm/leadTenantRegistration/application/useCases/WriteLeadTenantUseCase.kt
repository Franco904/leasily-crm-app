package com.fstengineering.leasilycrm.leadTenantRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.leadTenantRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantCommandRepository

class WriteLeadTenantUseCase(
    private val leadTenantCommandRepository: LeadTenantCommandRepository,
) : CommandUseCase<LeadTenantCommandModel> {
    override suspend fun invoke(
        command: LeadTenantCommandModel,
    ): AppResult<Unit, BaseError> {
        val leadTenant = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return leadTenantCommandRepository.insertOrUpdate(leadTenant)
    }
}
