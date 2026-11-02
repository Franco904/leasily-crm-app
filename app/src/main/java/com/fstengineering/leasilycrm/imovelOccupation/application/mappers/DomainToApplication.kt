package com.fstengineering.leasilycrm.imovelOccupation.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelOccupiedDate
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelOccupiedDateReadModel

fun ImovelOccupiedDate.toReadModel(tenantName: String?) =
    ImovelOccupiedDateReadModel(
        date = date,
        firstHalfPeriodIndex = firstHalfPeriodIndex,
        secondHalfPeriodIndex = secondHalfPeriodIndex,
        periodId = periodId,
        tenantName = tenantName,
    )
