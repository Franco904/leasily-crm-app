package com.fstengineering.leasilycrm.imovelOcupacao.infra.mappers

import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.infra.projections.PeriodoOcupadoProjection

fun PeriodoOcupadoProjection.toPeriodoOcupadoRawReadModel() = ImovelPeriodoOcupadoRawReadModel(
    id = id,
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
