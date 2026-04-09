package com.fstengineering.leasilycrm.propertyDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyDetails.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDetails.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyDetails.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyDetails.application.repositories.PropertyQueryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchPropertyUseCase(
    private val propertyQueryRepository: PropertyQueryRepository,
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
) : QueryFlowUseCase<PropertyArgs, PropertyReadModel> {
    override suspend fun invoke(
        args: PropertyArgs,
    ): Flow<AppResult<PropertyReadModel, ApplicationError>> {
        return propertyQueryRepository.watchById(args.id)
            .map { storedPropertyResult ->
                val propertyRaw = storedPropertyResult
                    .fold(
                        onError = { e -> return@map AppResult.Error(error = e) },
                        onSuccess = { it },
                    )

                val propertyOwnerName = propertyOwnerQueryRepository.getNameById(
                    propertyRaw.ownerId,
                )
                    .fold(
                        onError = { e -> return@map AppResult.Error(error = e) },
                        onSuccess = { name -> name },
                    )

                val updatedProperty = PropertyReadModel.create(
                    propertyRaw = propertyRaw,
                    ownerName = propertyOwnerName,
                )

                AppResult.Success(data = updatedProperty)
            }
    }
}
