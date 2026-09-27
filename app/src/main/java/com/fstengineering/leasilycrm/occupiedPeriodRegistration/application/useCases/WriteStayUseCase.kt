package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodCommandRepository

class WriteStayUseCase(
    private val occupiedPeriodCommandRepository: OccupiedPeriodCommandRepository,
) : CommandUseCase<StayCommandModel> {
    override suspend fun invoke(
        command: StayCommandModel,
    ): AppResult<Unit, BaseError> {
        val stay = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return occupiedPeriodCommandRepository.insertOrUpdate(stay)
    }
}
