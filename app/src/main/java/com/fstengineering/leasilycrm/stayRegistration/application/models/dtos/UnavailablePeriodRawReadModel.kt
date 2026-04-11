package com.fstengineering.leasilycrm.stayRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class UnavailablePeriodRawReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long,
    val checkInDateTime: AppDateTime?,
    val checkOutDateTime: AppDateTime?,
    val observations: String?,
) : QueryUseCase.ReadModel
