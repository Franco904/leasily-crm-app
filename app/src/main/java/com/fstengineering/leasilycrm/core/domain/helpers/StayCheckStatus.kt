package com.fstengineering.leasilycrm.core.domain.helpers

data class StayCheckStatus(
    val tenantName: String?,
    val propertyTypeId: Long?,
    val propertyOwnerName: String?,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
    val isCheckInDone: Boolean,
)
