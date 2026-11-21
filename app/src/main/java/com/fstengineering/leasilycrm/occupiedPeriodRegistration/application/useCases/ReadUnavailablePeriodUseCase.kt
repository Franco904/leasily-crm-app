package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.UnavailablePeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository

class ReadUnavailablePeriodUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val imovelQueryRepository: ImovelQueryRepository,
) {
    suspend operator fun invoke(
        args: UnavailablePeriodArgs,
    ): AppResult<UnavailablePeriodReadModel> {
        val unavailablePeriodRaw = args.id?.let {
            occupiedPeriodQueryRepository.getUnavailablePeriodById(args.id)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedUnavailablePeriod -> storedUnavailablePeriod },
                )
        }

        val imovel = unavailablePeriodRaw?.let {
            imovelQueryRepository.getById(it.imovelId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedImovel -> storedImovel },
                )
        }

        val updatedReadModel = UnavailablePeriodReadModel.create(
            unavailablePeriodRaw = unavailablePeriodRaw,
            imovel = imovel,
        )

        return AppResult.Success(data = updatedReadModel)
    }
}
