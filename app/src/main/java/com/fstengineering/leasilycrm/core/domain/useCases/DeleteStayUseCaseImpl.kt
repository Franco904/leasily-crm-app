package com.fstengineering.leasilycrm.core.domain.useCases

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteStayUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class DeleteStayUseCaseImpl(
    private val stayRepository: StayRepository,
    private val tenantRepository: TenantRepository,
) : DeleteStayUseCase {
    override suspend operator fun invoke(
        stay: Stay,
    ): AppResult<Unit, DataSourceError> {
        stayRepository.delete(id = stay.id!!)
            .onError { return AppResult.Error(error = it) }

        val leadTenant = stay.tenant
        val mustUpdateTenantStatus = leadTenant != null
                && !leadTenant.hasUnfinishedStay()
                && !stay.isUnavailablePeriod

        if (mustUpdateTenantStatus) {
            val newStatus =
                if (leadTenant.hasFinishedStays()) TenantStatus.Old
                else TenantStatus.Lead

            val updatedLeadTenant = leadTenant.copy(status = newStatus.id)

            tenantRepository.insertOrUpdate(updatedLeadTenant)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
