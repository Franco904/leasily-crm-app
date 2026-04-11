package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate

data class PropertyOccupiedDate(
    val date: AppDate,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
    val tenantId: Long?,
)
