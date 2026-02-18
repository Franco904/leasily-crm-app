package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.helpers.StayOwnedTenant
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.application.repositories.StayRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class RegisterStayCheckOutUseCaseImpl(
    private val stayRepository: StayRepository,
    private val assistanceRepository: AssistanceRepository,
) : RegisterStayCheckOutUseCase {
    override suspend operator fun invoke(stay: Stay.Owned): AppResult<Unit, DataError> {
        val updatedStay = stay.registerCheckOut()

        stayRepository.insertOrUpdate(stay = updatedStay)
            .onError { return AppResult.Error(error = it) }

        if (stay.tenant !is StayOwnedTenant.WithData) return AppResult.Success(data = Unit)

        // TODO: Create AssistanceReadView to determine isFinished flag
        val updatedAssistance = stay.tenant.data?.assistances
            ?.firstOrNull { !it.isFinished }
            ?.finish()

        if (updatedAssistance != null) {
            assistanceRepository.insertOrUpdate(updatedAssistance)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
