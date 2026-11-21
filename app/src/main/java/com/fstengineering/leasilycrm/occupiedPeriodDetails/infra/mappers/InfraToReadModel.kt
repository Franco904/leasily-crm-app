package com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.StayFileReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.TenantReadModel
import kotlinx.collections.immutable.toPersistentList

fun StayCompound.toRawReadModel(): OccupiedPeriodRawReadModel {
    val files = files
        .map { it.toReadModel() }
        .toPersistentList()

    return OccupiedPeriodRawReadModel(
        id = requireNotNull(stay.id),
        imovelId = stay.imovelId,
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

fun ImovelCompound.toReadModel() = ImovelReadModel(
    id = requireNotNull(imovel.id),
    mainPictureUriString = files
        .find { it.isMainPicture }
        ?.imovelFileUri,
    neighborhood = imovel.neighborhood,
    streetAddress = imovel.streetAddress,
    number = imovel.number,
    type = requireNotNull(ImovelType.fromId(imovel.typeId)),
)

private fun StayFileEntity.toReadModel() = StayFileReadModel(
    uriString = stayFileUri,
    name = stayFileName,
)
