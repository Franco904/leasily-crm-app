package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WritePropertyOwnerUseCase(
    private val propertyOwnerCommandRepository: PropertyOwnerCommandRepository,
) : CommandUseCase<PropertyOwnerCommandModel> {
    override suspend fun invoke(
        command: PropertyOwnerCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val propertyOwner = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        propertyOwnerCommandRepository.insertOrUpdate(propertyOwner)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
