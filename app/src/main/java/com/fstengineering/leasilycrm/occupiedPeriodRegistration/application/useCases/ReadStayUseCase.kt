package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.StayArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.services.ImovelOccupiedDatesService
import kotlinx.collections.immutable.persistentListOf

class ReadStayUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val imovelQueryRepository: ImovelQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
) {
    suspend operator fun invoke(
        args: StayArgs,
    ): AppResult<StayReadModel, ApplicationError> {
        val stayRaw = args.id?.let {
            occupiedPeriodQueryRepository.getStayById(args.id)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedStay -> storedStay },
                )
        }

        val property = stayRaw?.imovelId?.let {
            imovelQueryRepository.getById(stayRaw.imovelId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedImovel -> storedImovel },
                )
        }

        val propertyOccupiedPeriods = property?.id?.let { imovelId ->
            occupiedPeriodQueryRepository.getOccupiedPeriodsByPropertyId(imovelId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { it },
                )
        } ?: persistentListOf()

        val propertyOccupiedDates = ImovelOccupiedDatesService.getOccupiedDates(
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
