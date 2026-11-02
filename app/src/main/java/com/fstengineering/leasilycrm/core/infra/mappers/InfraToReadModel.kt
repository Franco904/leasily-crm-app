package com.fstengineering.leasilycrm.core.infra.mappers

import com.fstengineering.leasilycrm.core.application.models.OccupiedPeriodNotificationReadModel
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodNotificationProjection

fun OccupiedPeriodNotificationProjection.toReadModel() =
    OccupiedPeriodNotificationReadModel(
        tenantName = tenantName,
        propertyType = PropertyType.fromId(propertyTypeId) ?: PropertyType.Apartment,
        proprietarioName = proprietarioName,
        hourMinute = if (isCheckInToday) checkInDateTime.toHourMinute() else checkOutDateTime.toHourMinute(),
        isCheckInToday = isCheckInToday,
        isCheckOutToday = isCheckOutToday,
    )
