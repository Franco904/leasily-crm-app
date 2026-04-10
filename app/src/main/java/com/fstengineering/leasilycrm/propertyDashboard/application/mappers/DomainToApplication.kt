package com.fstengineering.leasilycrm.propertyDashboard.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedPeriodDate
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodDateReadModel

fun PropertyOccupiedPeriodDate.toReadModel(tenantName: String?) =
    PropertyOccupiedPeriodDateReadModel(
        date = date,
        firstHalfPeriodIndex = firstHalfPeriodIndex,
        secondHalfPeriodIndex = secondHalfPeriodIndex,
        periodId = periodId,
        tenantName = tenantName,
    )
