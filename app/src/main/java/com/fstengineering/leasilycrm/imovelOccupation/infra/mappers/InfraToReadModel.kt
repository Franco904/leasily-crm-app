package com.fstengineering.leasilycrm.imovelOccupation.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelPeriodoOcupadoRawReadModel

fun StayEntity.toRawReadModel() = ImovelPeriodoOcupadoRawReadModel(
    id = requireNotNull(id),
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
