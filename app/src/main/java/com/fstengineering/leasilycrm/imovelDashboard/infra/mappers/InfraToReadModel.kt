package com.fstengineering.leasilycrm.imovelDashboard.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelRawReadModel
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ImovelDashboardProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.PeriodoOcupadoProjection

fun ImovelDashboardProjection.toImovelRawReadModel() = ImovelRawReadModel(
    id = id,
    proprietarioId = proprietarioId,
    type = requireNotNull(ImovelType.fromId(typeId)),
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    number = number,
    mainPictureUriString = mainPictureUriString,
)

fun PeriodoOcupadoProjection.toPeriodoOcupadoRawReadModel() = ImovelPeriodoOcupadoRawReadModel(
    id = id,
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
