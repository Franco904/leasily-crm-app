package com.fstengineering.leasilycrm.core.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

data class OccupiedPeriodNotificationReadModel(
    val tenantName: String,
    val imovelType: ImovelType,
    val proprietarioName: String,
    val hourMinute: Pair<Int, Int>,
    val isCheckInToday: Boolean,
    val isCheckOutToday: Boolean,
)
