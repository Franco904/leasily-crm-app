package com.fstengineering.leasilycrm.propertyStays.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel

fun StayEntity.toRawReadModel() = PropertyOccupiedPeriodRawReadModel(
    id = requireNotNull(id),
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
