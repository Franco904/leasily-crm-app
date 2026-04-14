package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.UnavailablePeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository

class ReadUnavailablePeriodUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val propertyQueryRepository: PropertyQueryRepository,
) : QueryUseCase<UnavailablePeriodArgs, UnavailablePeriodReadModel> {
    override suspend fun invoke(
        args: UnavailablePeriodArgs,
    ): AppResult<UnavailablePeriodReadModel, ApplicationError> {
        val unavailablePeriodRaw = args.id?.let {
            occupiedPeriodQueryRepository.getUnavailablePeriodById(args.id)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedUnavailablePeriod -> storedUnavailablePeriod },
                )
        }

        val property = unavailablePeriodRaw?.let {
            propertyQueryRepository.getById(it.propertyId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedProperty -> storedProperty },
                )
        }

        val updatedReadModel = UnavailablePeriodReadModel.create(
            unavailablePeriodRaw = unavailablePeriodRaw,
            property = property,
        )

        return AppResult.Success(data = updatedReadModel)
    }
}
