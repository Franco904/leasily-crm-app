package com.fstengineering.leasilycrm.imovelOccupation.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelOccupiedPeriod
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelOccupiedPeriodRawReadModel

fun ImovelOccupiedPeriodRawReadModel.toValueObject() = ImovelOccupiedPeriod(
    id = id,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    tenantId = tenantId,
)
