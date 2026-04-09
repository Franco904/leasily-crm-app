package com.fstengineering.leasilycrm.stayRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.queries.SelectablePropertiesArgs
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.domain.services.PropertyOccupiedDatesPolicy
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class ReadSelectablePropertiesUseCase(
    private val propertyQueryRepository: PropertyQueryRepository,
    private val stayQueryRepository: StayQueryRepository,
) : QueryUseCase<SelectablePropertiesArgs, ImmutableList<PropertyReadModel>> {
    override suspend fun invoke(
        args: SelectablePropertiesArgs,
    ): AppResult<ImmutableList<PropertyReadModel>, ApplicationError> {
        val properties = propertyQueryRepository.getAll(args.matching)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedProperty -> storedProperty },
            )

        val propertyWithOccupiedPeriods = properties
            .map { property ->
                val occupiedPeriods =
                    stayQueryRepository.getOccupiedPeriodsByPropertyId(requireNotNull(property.id))
                        .fold(
                            onError = { e -> return AppResult.Error(error = e) },
                            onSuccess = { it },
                        )

                val occupiedDates = PropertyOccupiedDatesPolicy.getOccupiedDates(
                    occupiedPeriods = occupiedPeriods,
                    stayToEditId = args.stayId,
                )

                property.copy(occupiedDates = occupiedDates)
            }
            .toPersistentList()

        return AppResult.Success(data = propertyWithOccupiedPeriods)
    }
}
