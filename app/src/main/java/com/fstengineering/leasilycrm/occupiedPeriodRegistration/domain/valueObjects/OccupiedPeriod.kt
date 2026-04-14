package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class OccupiedPeriod(
    val stayId: Long,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
)
