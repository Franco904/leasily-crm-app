package com.fstengineering.leasilycrm.imovelOcupacao.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.ImovelCalendarOccupiedDateUiState

data class ImovelOccupiedDateUiState(
    val dayOfMonth: String,
    val year: Int,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
    val tenantName: String?,
) {
    val isUnavailablePeriod = tenantName == null

    fun toCalendarState() = ImovelCalendarOccupiedDateUiState(
        dayOfMonth = dayOfMonth,
        firstHalfPeriodIndex = firstHalfPeriodIndex,
        secondHalfPeriodIndex = secondHalfPeriodIndex,
        periodId = periodId,
    )
}
