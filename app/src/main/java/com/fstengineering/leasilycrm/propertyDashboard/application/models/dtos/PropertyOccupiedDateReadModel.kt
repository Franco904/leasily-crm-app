package com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

data class PropertyOccupiedDateReadModel(
    val date: AppDate,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
    val tenantName: String?,
)
