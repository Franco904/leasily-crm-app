package com.fstengineering.leasilycrm.imovelDashboard.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelOccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelRawReadModel

fun ImovelCompound.toRawReadModel() = ImovelRawReadModel(
    id = requireNotNull(imovel.id),
    proprietarioId = imovel.proprietarioId,
    type = requireNotNull(ImovelType.fromId(imovel.typeId)),
    neighborhood = imovel.neighborhood,
    streetAddress = imovel.streetAddress,
    number = imovel.number,
    mainPictureUriString = files.find { it.isMainPicture }?.imovelFileUri,
)

fun StayEntity.toRawReadModel() = ImovelOccupiedPeriodRawReadModel(
    id = requireNotNull(id),
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
