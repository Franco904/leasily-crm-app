package com.fstengineering.leasilycrm.propertyRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyCommandRepository

class WritePropertyUseCase(
    private val propertyCommandRepository: PropertyCommandRepository,
) : CommandUseCase<PropertyCommandModel> {
    override suspend fun invoke(
        command: PropertyCommandModel,
    ): AppResult<Unit, BaseError> {
        val property = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return propertyCommandRepository.insertOrUpdate(property)
    }
}
