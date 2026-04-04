package com.fstengineering.leasilycrm.stayRegistration.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayQueryModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.TenantQueryRepository

class ReadStayUseCase(
    private val stayQueryRepository: StayQueryRepository,
    private val propertyQueryRepository: PropertyQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
) : QueryUseCase<StayQueryModel, StayReadModel> {
    override suspend fun invoke(
        query: StayQueryModel,
    ): AppResult<StayReadModel, ApplicationError> {
        val stayRaw = query.id?.let {
            stayQueryRepository.getStayById(query.id)
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

        val propertyCurrentStays = property?.id?.let { propertyId ->
            stayQueryRepository.getAllByPropertyId(propertyId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { it },
                )
        }

        val propertyWithCurrentStays = propertyCurrentStays?.let {
            property.copy(staysDates = it)
        }

        val tenant = stayRaw?.tenantId?.let {
            tenantQueryRepository.getById(stayRaw.tenantId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedTenant -> storedTenant },
                )
        }

        val updatedReadModel = StayReadModel.create(
            stayRaw = stayRaw,
            property = propertyWithCurrentStays,
            tenant = tenant,
        )

        return AppResult.Success(data = updatedReadModel)
    }
}
