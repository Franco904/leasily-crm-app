package com.fstengineering.leasilycrm.core.domain.valueObjects

data class PropertyOccupiedDate(
    val date: AppDate,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
    val tenantId: Long?,
)
