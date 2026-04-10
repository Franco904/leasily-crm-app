package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

data class PropertyOccupiedPeriod(
    val id: Long,
    val tenantId: Long?,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
) {
    val isUnavailablePeriod get() = tenantId == null
}
