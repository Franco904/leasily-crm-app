package com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

data class ImovelOccupiedDateReadModel(
    val date: AppDate,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
    val tenantName: String?,
)
