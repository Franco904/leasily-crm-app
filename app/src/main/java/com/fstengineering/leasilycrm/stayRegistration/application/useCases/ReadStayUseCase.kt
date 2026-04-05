package com.fstengineering.leasilycrm.stayRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayArgs
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.domain.services.PropertyOccupiedDatesPolicy
import kotlinx.collections.immutable.persistentListOf

class ReadStayUseCase(
    private val stayQueryRepository: StayQueryRepository,
    private val propertyQueryRepository: PropertyQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
) : QueryUseCase<StayArgs, StayReadModel> {
    override suspend fun invoke(
        args: StayArgs,
    ): AppResult<StayReadModel, ApplicationError> {
        val stayRaw = args.id?.let {
            stayQueryRepository.getStayById(args.id)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedStay -> storedStay },
                )
        }

        val property = stayRaw?.propertyId?.let {
            propertyQueryRepository.getById(stayRaw.propertyId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedProperty -> storedProperty },
                )
        }

        val propertyOccupiedPeriods = property?.id?.let { propertyId ->
            stayQueryRepository.getOccupiedPeriodsByPropertyId(propertyId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { it },
                )
        } ?: persistentListOf()

        val propertyOccupiedDates = PropertyOccupiedDatesPolicy.getOccupiedDates(
            occupiedPeriods = propertyOccupiedPeriods,
            stayToEditId = args.id,
        )

        val tenant = stayRaw?.tenantId?.let {
            tenantQueryRepository.getById(stayRaw.tenantId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedTenant -> storedTenant },
                )
        }

        val updatedReadModel = StayReadModel.create(
            stayRaw = stayRaw,
            property = property?.copy(occupiedDates = propertyOccupiedDates),
            tenant = tenant,
        )

        return AppResult.Success(data = updatedReadModel)
    }
}
