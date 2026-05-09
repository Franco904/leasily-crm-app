package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WriteUnavailablePeriodUseCase(
    private val occupiedPeriodCommandRepository: OccupiedPeriodCommandRepository,
) : CommandUseCase<UnavailablePeriodCommandModel> {
    override suspend fun invoke(
        command: UnavailablePeriodCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val unavailablePeriod = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        occupiedPeriodCommandRepository.insertOrUpdate(unavailablePeriod)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
