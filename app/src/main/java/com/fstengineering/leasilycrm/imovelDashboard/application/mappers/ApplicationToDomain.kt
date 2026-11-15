package com.fstengineering.leasilycrm.imovelDashboard.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelPeriodoOcupadoRawReadModel

fun ImovelPeriodoOcupadoRawReadModel.toValueObject() = ImovelPeriodoOcupado(
    id = id,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    tenantId = tenantId,
)
