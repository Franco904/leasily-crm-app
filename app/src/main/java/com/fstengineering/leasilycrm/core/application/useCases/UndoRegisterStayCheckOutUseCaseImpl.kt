package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.StaySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.utils.InfraError

class UndoRegisterStayCheckOutUseCaseImpl(
    private val staySharedCommandRepository: StaySharedCommandRepository,
    private val assistanceCommandRepository: AssistanceCommandRepository,
) : UndoRegisterStayCheckOutUseCase {
    override suspend operator fun invoke(stay: Stay.Owned): AppResult<Unit, InfraError> {
        val updatedStay = stay.undoRegisterCheckOut()

        staySharedCommandRepository.insertOrUpdate(stay = updatedStay)
            .onError { return AppResult.Error(error = it) }

        val updatedAssistance = stay.tenant.data?.assistances
            ?.firstOrNull { !it.isFinished }
            ?.unfinish()

        if (updatedAssistance != null) {
            assistanceCommandRepository.insertOrUpdate(updatedAssistance)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
