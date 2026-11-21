package com.fstengineering.leasilycrm.imovelDashboard.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelSummaryReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ImovelDashboardProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.PeriodoOcupadoProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ProprietarioProjection

fun ImovelDashboardProjection.toReadModel() = ImovelSummaryReadModel(
    id = id,
    proprietario = requireNotNull(proprietario?.takeUnless { it.isDeleted }).toReadModel(),
    type = requireNotNull(ImovelType.fromId(typeId)),
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    number = number,
    mainPictureUriString = mainPictureUriString,
)

fun PeriodoOcupadoProjection.toReadModel() = ImovelPeriodoOcupadoRawReadModel(
    id = id,
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)

private fun ProprietarioProjection.toReadModel() = ProprietarioReadModel(id = id, name = name)
