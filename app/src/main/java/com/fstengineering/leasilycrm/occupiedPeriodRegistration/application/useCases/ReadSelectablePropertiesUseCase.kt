package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.SelectablePropertiesArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.services.ImovelOccupiedDatesService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class ReadSelectablePropertiesUseCase(
    private val imovelQueryRepository: ImovelQueryRepository,
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
) {
    suspend operator fun invoke(
        args: SelectablePropertiesArgs,
    ): AppResult<ImmutableList<ImovelReadModel>, ApplicationError> {
        val properties = imovelQueryRepository.getAll(args.matching)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedImovel -> storedImovel },
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

                val occupiedDates = ImovelOccupiedDatesService.getOccupiedDates(
                    occupiedPeriods = occupiedPeriods,
                    stayToEditId = args.stayId,
                )

                property.copy(occupiedDates = occupiedDates)
            }
            .toPersistentList()

        return AppResult.Success(data = propertyWithOccupiedPeriods)
    }
}
