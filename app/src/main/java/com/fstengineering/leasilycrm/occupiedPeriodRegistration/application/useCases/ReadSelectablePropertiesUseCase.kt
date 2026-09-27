package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.SelectablePropertiesArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.services.PropertyOccupiedDatesService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class ReadSelectablePropertiesUseCase(
    private val propertyQueryRepository: PropertyQueryRepository,
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
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
                    occupiedPeriodQueryRepository.getOccupiedPeriodsByPropertyId(
                        requireNotNull(
                            property.id
                        )
                    )
                        .fold(
                            onError = { e -> return AppResult.Error(error = e) },
                            onSuccess = { it },
                        )

                val occupiedDates = PropertyOccupiedDatesService.getOccupiedDates(
                    occupiedPeriods = occupiedPeriods,
                    stayToEditId = args.stayId,
                )

                property.copy(occupiedDates = occupiedDates)
            }
            .toPersistentList()

        return AppResult.Success(data = propertyWithOccupiedPeriods)
    }
}
