package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppLazyDateTime

data class StayCheckStatus(
    val tenantName: String?,
    val propertyTypeName: String?,
    val propertyOwnerName: String?,
    val checkInDateTime: AppLazyDateTime,
    val checkOutDateTime: AppLazyDateTime,
    val isCheckInDone: Boolean,
)
