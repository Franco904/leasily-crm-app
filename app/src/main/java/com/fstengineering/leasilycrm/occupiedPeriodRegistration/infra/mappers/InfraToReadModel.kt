package com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayFileReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodRawReadModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList

fun StayCompound.toStayRawReadModel(): StayRawReadModel {
    val files = files
        .map { it.toReadModel() }
        .toPersistentList()

    return StayRawReadModel(
        id = stay.id,
        createDateTime = stay.createDateTime,
        propertyId = stay.propertyId,
        tenantId = requireNotNull(stay.tenantId),
        vehicleMake = stay.vehicleMake,
        vehicleColor = stay.vehicleColor,
        vehicleLicensePlate = stay.vehicleLicensePlate,
        guests = requireNotNull(stay.guests),
        checkInDateTime = stay.checkInDateTime,
        checkInTime = stay.checkInDateTime.hour to stay.checkInDateTime.minute,
        checkOutDateTime = stay.checkOutDateTime,
        checkOutTime = stay.checkOutDateTime.hour to stay.checkOutDateTime.minute,
        isAutoCheckIn = requireNotNull(stay.isAutoCheckIn),
        isAutoCheckOut = requireNotNull(stay.isAutoCheckOut),
        totalPrice = stay.totalPrice,
        commission = stay.commission,
        files = files,
        observations = stay.observations,
    )
}

fun StayCompound.toUnavailablePeriodRawReadModel() = UnavailablePeriodRawReadModel(
    id = stay.id,
    createDateTime = stay.createDateTime,
    propertyId = stay.propertyId,
    checkInDateTime = stay.checkInDateTime,
    checkOutDateTime = stay.checkOutDateTime,
    observations = stay.observations,
)

fun StayEntity.toCheckInCheckOutReadModel() = OccupiedPeriodReadModel(
    stayId = requireNotNull(id),
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
)

private fun StayFileEntity.toReadModel() = StayFileReadModel(
    id = id,
    uriString = stayFileUri,
    name = stayFileName,
)

fun PropertyCompound.toReadModel() = PropertyReadModel(
    id = property.id,
    type = requireNotNull(PropertyType.fromId(property.typeId)),
    neighborhood = property.neighborhood,
    streetAddress = property.streetAddress,
    number = property.number,
    mainPictureUriString = files
        .find { picture -> picture.isMainPicture }
        ?.propertyFileUri,
    occupiedDates = persistentSetOf(),
)

fun LeadTenantReadView.toReadModel() = TenantReadModel(
    id = id,
    name = name,
    city = city,
)
