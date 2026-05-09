package com.fstengineering.leasilycrm.propertyDashboard.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyRawReadModel

fun PropertyCompound.toRawReadModel() = PropertyRawReadModel(
    id = requireNotNull(property.id),
    ownerId = property.ownerId,
    type = requireNotNull(PropertyType.fromId(property.typeId)),
    neighborhood = property.neighborhood,
    streetAddress = property.streetAddress,
    number = property.number,
    mainPictureUriString = files.find { it.isMainPicture }?.propertyFileUri,
)

fun StayEntity.toRawReadModel() = PropertyOccupiedPeriodRawReadModel(
    id = requireNotNull(id),
    tenantId = tenantId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)
