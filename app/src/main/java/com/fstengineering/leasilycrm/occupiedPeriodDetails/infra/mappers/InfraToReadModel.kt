package com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.StayFileReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.TenantReadModel
import kotlinx.collections.immutable.toPersistentList

fun StayCompound.toRawReadModel(): OccupiedPeriodRawReadModel {
    val files = files
        .map { it.toReadModel() }
        .toPersistentList()

    return OccupiedPeriodRawReadModel(
        id = requireNotNull(stay.id),
        propertyId = stay.propertyId,
        tenantId = stay.tenantId,
        vehicleMake = stay.vehicleMake,
        vehicleColor = stay.vehicleColor,
        vehicleLicensePlate = stay.vehicleLicensePlate,
        guests = stay.guests,
        checkInDateTime = stay.checkInDateTime,
        checkInHourMinute = stay.checkInDateTime.hour to stay.checkInDateTime.minute,
        checkOutDateTime = stay.checkOutDateTime,
        checkOutHourMinute = stay.checkOutDateTime.hour to stay.checkOutDateTime.minute,
        isAutoCheckIn = stay.isAutoCheckIn,
        isAutoCheckOut = stay.isAutoCheckOut,
        totalPrice = stay.totalPrice,
        commission = stay.commission,
        observations = stay.observations,
        isCheckInDone = stay.status == 1 || stay.status == 2,
        isCheckOutDone = stay.status == 2,
        files = files,
    )
}

fun PropertyCompound.toReadModel() = PropertyReadModel(
    id = requireNotNull(property.id),
    mainPictureUriString = files
        .find { it.isMainPicture }
        ?.propertyFileUri,
    neighborhood = property.neighborhood,
    streetAddress = property.streetAddress,
    number = property.number,
    type = requireNotNull(PropertyType.fromId(property.typeId)),
)

fun LeadTenantReadView.toReadModel() = TenantReadModel(
    id = requireNotNull(id),
    name = name,
    city = city,
)

private fun StayFileEntity.toReadModel() = StayFileReadModel(
    id = id,
    uriString = stayFileUri,
    name = stayFileName,
)
