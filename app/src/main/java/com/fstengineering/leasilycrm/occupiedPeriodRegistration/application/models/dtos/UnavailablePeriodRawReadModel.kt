package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class UnavailablePeriodRawReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long,
    val checkInDateTime: AppDateTime?,
    val checkOutDateTime: AppDateTime?,
    val observations: String?,
)
