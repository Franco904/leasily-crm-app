package com.fstengineering.leasilycrm.propertyDashboard.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedPeriod
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel

fun PropertyOccupiedPeriodRawReadModel.toValueObject() = PropertyOccupiedPeriod(
    id = id,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    tenantId = tenantId,
)
