package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.application.repositories.StayRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class UndoRegisterStayCheckOutUseCaseImpl(
    private val stayRepository: StayRepository,
    private val assistanceRepository: AssistanceRepository,
) : UndoRegisterStayCheckOutUseCase {
    override suspend operator fun invoke(stay: Stay.Owned): AppResult<Unit, DataError> {
        val updatedStay = stay.undoRegisterCheckOut()

        stayRepository.insertOrUpdate(stay = updatedStay)
            .onError { return AppResult.Error(error = it) }

        if (stay.tenant !is StayOwnedTenant.WithData) return AppResult.Success(data = Unit)

        val updatedAssistance = stay.tenant.data?.assistances
            ?.firstOrNull { !it.isFinished }
            ?.unfinish()

        if (updatedAssistance != null) {
            assistanceRepository.insertOrUpdate(updatedAssistance)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
