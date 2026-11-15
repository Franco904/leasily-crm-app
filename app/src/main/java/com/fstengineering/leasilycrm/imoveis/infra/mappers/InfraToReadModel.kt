package com.fstengineering.leasilycrm.imoveis.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imoveis.infra.projections.ImovelCardProjection

fun ImovelCardProjection.toImovelReadModel() = ImovelReadModel(
    id = id,
    mainPictureUriString = mainPictureUriString,
    type = requireNotNull(ImovelType.fromId(typeId)),
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    number = number,
)
