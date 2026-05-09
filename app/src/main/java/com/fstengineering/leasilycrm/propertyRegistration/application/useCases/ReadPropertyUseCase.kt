package com.fstengineering.leasilycrm.propertyRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyQueryRepository

class ReadPropertyUseCase(
    private val propertyQueryRepository: PropertyQueryRepository,
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
) : QueryUseCase<PropertyArgs, PropertyReadModel> {
    override suspend fun invoke(
        args: PropertyArgs,
    ): AppResult<PropertyReadModel, ApplicationError> {
        val propertyRaw = propertyQueryRepository.getById(args.id)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedProperty -> storedProperty },
            )

        val propertyOwner = propertyOwnerQueryRepository.getById(propertyRaw.ownerId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedPropertyOwner -> storedPropertyOwner },
            )

        val updatedReadModel = PropertyReadModel.create(
            owner = propertyOwner,
            propertyRaw = propertyRaw,
        )

        return AppResult.Success(data = updatedReadModel)
    }
}
