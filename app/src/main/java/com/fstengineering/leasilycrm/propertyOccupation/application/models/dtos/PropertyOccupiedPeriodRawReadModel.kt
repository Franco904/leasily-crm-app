package com.fstengineering.leasilycrm.propertyOccupation.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class PropertyOccupiedPeriodRawReadModel(
    val id: Long,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
    val tenantId: Long?,
) {
    val isUnavailablePeriod get() = tenantId != null
}
