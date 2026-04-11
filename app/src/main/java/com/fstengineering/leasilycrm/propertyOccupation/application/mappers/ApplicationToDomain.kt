package com.fstengineering.leasilycrm.propertyOccupation.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedPeriod
import com.fstengineering.leasilycrm.propertyOccupation.application.models.dtos.PropertyOccupiedPeriodRawReadModel

fun PropertyOccupiedPeriodRawReadModel.toValueObject() = PropertyOccupiedPeriod(
    id = id,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    tenantId = tenantId,
)
