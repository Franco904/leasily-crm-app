package com.fstengineering.leasilycrm.imovelOccupation.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelOccupiedPeriodRawReadModel

fun StayEntity.toRawReadModel() = ImovelOccupiedPeriodRawReadModel(
    id = requireNotNull(id),
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
