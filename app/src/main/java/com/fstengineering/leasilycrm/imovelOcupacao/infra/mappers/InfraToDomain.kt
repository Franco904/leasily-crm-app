package com.fstengineering.leasilycrm.imovelOcupacao.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.imovelOcupacao.infra.projections.PeriodoOcupadoProjection

fun PeriodoOcupadoProjection.toValueObject() = ImovelPeriodoOcupado(
    id = id,
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
