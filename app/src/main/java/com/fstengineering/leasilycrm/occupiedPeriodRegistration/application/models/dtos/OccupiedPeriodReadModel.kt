package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class OccupiedPeriodReadModel(
    val stayId: Long,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
)