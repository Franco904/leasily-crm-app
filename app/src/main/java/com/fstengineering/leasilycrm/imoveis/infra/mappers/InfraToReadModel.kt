package com.fstengineering.leasilycrm.imoveis.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel

fun ImovelCompound.toReadModel() = ImovelReadModel(
    id = requireNotNull(imovel.id),
    type = requireNotNull(ImovelType.fromId(imovel.typeId)),
    neighborhood = imovel.neighborhood,
    streetAddress = imovel.streetAddress,
    number = imovel.number,
    mainPictureUriString = files
        .find { picture -> picture.isMainPicture }
        ?.imovelFileUri,
)
