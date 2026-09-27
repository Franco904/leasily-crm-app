package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerCommandRepository

class WritePropertyOwnerUseCase(
    private val propertyOwnerCommandRepository: PropertyOwnerCommandRepository,
) {
    suspend operator fun invoke(
        command: PropertyOwnerCommandModel,
    ): AppResult<Unit, BaseError> {
        val propertyOwner = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return propertyOwnerCommandRepository.insertOrUpdate(propertyOwner)
    }
}
