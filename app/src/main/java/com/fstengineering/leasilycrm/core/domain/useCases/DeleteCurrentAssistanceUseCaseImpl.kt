package com.fstengineering.leasilycrm.core.domain.useCases

import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteCurrentAssistanceUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class DeleteCurrentAssistanceUseCaseImpl(
    private val assistanceRepository: AssistanceRepository,
    private val tenantRepository: TenantRepository,
) : DeleteCurrentAssistanceUseCase {
    override suspend operator fun invoke(
        currentAssistance: Assistance,
        hasFinishedStays: Boolean,
    ): AppResult<Unit, DataSourceError> {
        assistanceRepository.delete(id = currentAssistance.id!!)
            .onError { return AppResult.Error(error = it) }

        val leadTenant = currentAssistance.tenant
        val isLead = leadTenant?.status == TenantStatus.Lead.id

        if (isLead && hasFinishedStays) {
            val updatedLeadTenant = leadTenant.copy(status = TenantStatus.Old.id)

            tenantRepository.insertOrUpdate(updatedLeadTenant)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
