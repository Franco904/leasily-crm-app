package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime

data class StayCheckStatus(
    val tenantName: String?,
    val propertyTypeName: String?,
    val propertyOwnerName: String?,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
    val isCheckInDone: Boolean,
)
