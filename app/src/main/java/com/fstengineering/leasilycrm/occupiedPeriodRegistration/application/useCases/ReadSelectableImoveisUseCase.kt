package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.SelectableImoveisArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.services.ImovelOccupiedDatesService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class ReadSelectableImoveisUseCase(
    private val imovelQueryRepository: ImovelQueryRepository,
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
) {
    suspend operator fun invoke(
        args: SelectableImoveisArgs,
    ): AppResult<ImmutableList<ImovelReadModel>, ApplicationError> {
        val imoveis = imovelQueryRepository.getAll(args.matching)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedImovel -> storedImovel },
            )

        val imovelWithOccupiedPeriods = imoveis
            .map { imovel ->
                val occupiedPeriods =
                    occupiedPeriodQueryRepository.getOccupiedPeriodsByImovelId(
                        requireNotNull(
                            imovel.id
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

                imovel.copy(occupiedDates = occupiedDates)
            }
            .toPersistentList()

        return AppResult.Success(data = imovelWithOccupiedPeriods)
    }
}
