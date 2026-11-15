package com.fstengineering.leasilycrm.imovelOcupacao.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelPeriodoOcupadoRawReadModel

fun ImovelPeriodoOcupadoRawReadModel.toValueObject() = ImovelPeriodoOcupado(
    id = id,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    tenantId = tenantId,
)
