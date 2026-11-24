package com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayFileReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayRawReadModel
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
        imovelId = stay.imovelId,
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
    imovelId = stay.imovelId,
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

fun ImovelCompound.toReadModel() = ImovelReadModel(
    id = imovel.id,
    type = requireNotNull(ImovelType.fromId(imovel.typeId)),
    neighborhood = imovel.neighborhood,
    streetAddress = imovel.streetAddress,
    number = imovel.number,
    mainPictureUriString = files
        .find { picture -> picture.isMainPicture }
        ?.imovelFileUri,
    occupiedDates = persistentSetOf(),
)
