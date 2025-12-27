package com.fstengineering.leasilycrm.core.domain.useCases

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class UndoRegisterStayCheckOutUseCaseImpl(
    private val stayRepository: StayRepository,
    private val assistanceRepository: AssistanceRepository,
) : UndoRegisterStayCheckOutUseCase {
    override suspend operator fun invoke(
        stay: Stay,
    ): AppResult<Unit, DataSourceError> {
        val updatedStay = stay.copy(isCheckOutDone = false)

        stayRepository.insertOrUpdate(stay = updatedStay)
            .onError { return AppResult.Error(error = it) }

        val unfinishedAssistance = stay.tenant?.assistances
            ?.firstOrNull { !it.isFinished }
            ?.copy(isFinished = false)

        if (unfinishedAssistance != null) {
            assistanceRepository.insertOrUpdate(unfinishedAssistance)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
