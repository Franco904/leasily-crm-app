package com.fstengineering.leasilycrm.leadTenantRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.leadTenantRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WriteLeadTenantUseCase(
    private val leadTenantCommandRepository: LeadTenantCommandRepository,
) : CommandUseCase<LeadTenantCommandModel> {
    override suspend fun invoke(
        command: LeadTenantCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val leadTenant = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        leadTenantCommandRepository.insertOrUpdate(leadTenant)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
