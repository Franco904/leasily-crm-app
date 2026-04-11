package com.fstengineering.leasilycrm.propertyDashboard.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedDate
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedDateReadModel

fun PropertyOccupiedDate.toReadModel(tenantName: String?) =
    PropertyOccupiedDateReadModel(
        date = date,
        firstHalfPeriodIndex = firstHalfPeriodIndex,
        secondHalfPeriodIndex = secondHalfPeriodIndex,
        periodId = periodId,
        tenantName = tenantName,
    )
