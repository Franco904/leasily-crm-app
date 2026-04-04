package com.fstengineering.leasilycrm.stayRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.propertyRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.stayRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WriteUnavailablePeriodUseCase(
    private val stayCommandRepository: StayCommandRepository,
) : CommandUseCase<UnavailablePeriodCommandModel> {
    override suspend fun invoke(
        command: UnavailablePeriodCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val unavailablePeriod = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        stayCommandRepository.insertOrUpdate(unavailablePeriod)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
