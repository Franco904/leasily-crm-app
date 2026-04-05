package com.fstengineering.leasilycrm.stayRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodArgs
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayQueryRepository

class ReadUnavailablePeriodUseCase(
    private val stayQueryRepository: StayQueryRepository,
    private val propertyQueryRepository: PropertyQueryRepository,
) : QueryUseCase<UnavailablePeriodArgs, UnavailablePeriodReadModel> {
    override suspend fun invoke(
        query: UnavailablePeriodArgs,
    ): AppResult<UnavailablePeriodReadModel, ApplicationError> {
        val unavailablePeriodRaw = query.id?.let {
            stayQueryRepository.getUnavailablePeriodById(query.id)
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
