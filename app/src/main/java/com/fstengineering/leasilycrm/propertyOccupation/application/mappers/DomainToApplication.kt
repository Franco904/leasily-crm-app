package com.fstengineering.leasilycrm.propertyOccupation.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedDate
import com.fstengineering.leasilycrm.propertyOccupation.application.models.dtos.PropertyOccupiedDateReadModel

fun PropertyOccupiedDate.toReadModel(tenantName: String?) =
    PropertyOccupiedDateReadModel(
        date = date,
        firstHalfPeriodIndex = firstHalfPeriodIndex,
        secondHalfPeriodIndex = secondHalfPeriodIndex,
        periodId = periodId,
        tenantName = tenantName,
    )
