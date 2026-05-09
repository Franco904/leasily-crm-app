package com.fstengineering.leasilycrm.properties.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.properties.application.models.dtos.PropertyReadModel

fun PropertyCompound.toReadModel() = PropertyReadModel(
    id = requireNotNull(property.id),
    type = requireNotNull(PropertyType.fromId(property.typeId)),
    neighborhood = property.neighborhood,
    streetAddress = property.streetAddress,
    number = property.number,
    mainPictureUriString = files
        .find { picture -> picture.isMainPicture }
        ?.propertyFileUri,
)
