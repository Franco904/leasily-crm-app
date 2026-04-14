package com.fstengineering.leasilycrm.core.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class StaySharedReadModel(
    val id: Long,
    val tenantId: Long?,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
    val isCheckInDone: Boolean,
    val isCheckOutDone: Boolean,
) {
    val isUnavailablePeriod get() = tenantId == null
}
