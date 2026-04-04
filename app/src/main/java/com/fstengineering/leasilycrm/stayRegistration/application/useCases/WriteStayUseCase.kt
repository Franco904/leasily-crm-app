package com.fstengineering.leasilycrm.stayRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.propertyRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.propertyRegistration.application.models.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyCommandRepository
import com.fstengineering.leasilycrm.stayRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WriteStayUseCase(
    private val stayCommandRepository: StayCommandRepository,
) : CommandUseCase<StayCommandModel> {
    override suspend fun invoke(
        command: StayCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val stay = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        stayCommandRepository.insertOrUpdate(stay)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
