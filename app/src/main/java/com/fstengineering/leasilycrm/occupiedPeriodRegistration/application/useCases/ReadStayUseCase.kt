package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.StayArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.services.PropertyOccupiedDatesService
import kotlinx.collections.immutable.persistentListOf

class ReadStayUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val propertyQueryRepository: PropertyQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
) : QueryUseCase<StayArgs, StayReadModel> {
    override suspend fun invoke(
        args: StayArgs,
    ): AppResult<StayReadModel, ApplicationError> {
        val stayRaw = args.id?.let {
            occupiedPeriodQueryRepository.getStayById(args.id)
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
            occupiedPeriodQueryRepository.getOccupiedPeriodsByPropertyId(propertyId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { it },
                )
        } ?: persistentListOf()

        val propertyOccupiedDates = PropertyOccupiedDatesService.getOccupiedDates(
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
