package com.fstengineering.leasilycrm.stayRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

data class OccupiedPeriod(
    val stayId: Long,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
)
