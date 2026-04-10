package com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

data class PropertyOccupiedPeriodRawReadModel(
    val id: Long,
    val tenantId: Long?,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
) {
    val isUnavailablePeriod get() = tenantId != null
}
