package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodCommandRepository

class WriteUnavailablePeriodUseCase(
    private val occupiedPeriodCommandRepository: OccupiedPeriodCommandRepository,
) {
    suspend operator fun invoke(
        command: UnavailablePeriodCommandModel,
    ): AppResult<Unit, BaseError> {
        val unavailablePeriod = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return occupiedPeriodCommandRepository.insertOrUpdate(unavailablePeriod)
    }
}
