package com.fstengineering.leasilycrm.propertyRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.propertyRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WritePropertyUseCase(
    private val propertyCommandRepository: PropertyCommandRepository,
) : CommandUseCase<PropertyCommandModel> {
    override suspend fun invoke(
        command: PropertyCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val property = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        propertyCommandRepository.insertOrUpdate(property)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
