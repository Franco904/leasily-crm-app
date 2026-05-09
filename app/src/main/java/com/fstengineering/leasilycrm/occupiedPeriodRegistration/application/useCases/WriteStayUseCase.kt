package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WriteStayUseCase(
    private val occupiedPeriodCommandRepository: OccupiedPeriodCommandRepository,
) : CommandUseCase<StayCommandModel> {
    override suspend fun invoke(
        command: StayCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val stay = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        occupiedPeriodCommandRepository.insertOrUpdate(stay)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
