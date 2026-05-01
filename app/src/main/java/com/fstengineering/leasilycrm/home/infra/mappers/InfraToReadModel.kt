package com.fstengineering.leasilycrm.home.infra.mappers

import com.fstengineering.leasilycrm.home.application.models.dtos.OccupiedPeriodListReadModel
import com.fstengineering.leasilycrm.core.application.models.OccupiedPeriodNotificationReadModel
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodListProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodNotificationProjection

fun OccupiedPeriodListProjection.toReadModel() = OccupiedPeriodListReadModel(
    id = id,
    tenantId = tenantId,
    tenantName = tenantName,
    propertyType = propertyTypeId?.let { PropertyType.fromId(it) },
    propertyNeighborhood = neighborhood,
    checkInDateTime = checkInDateTime,
    checkInHourMinute = checkInDateTime.hour to checkInDateTime.minute,
    checkOutDateTime = checkOutDateTime,
    checkOutHourMinute = checkOutDateTime.hour to checkOutDateTime.minute,
    isAutoCheckIn = isAutoCheckIn ?: false,
    isAutoCheckOut = isAutoCheckOut ?: false,
    isCheckInDone = (status ?: 0) >= 1,
    isCheckOutDone = (status ?: 0) >= 2,
)
