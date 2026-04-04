package com.fstengineering.leasilycrm.stayRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayFileCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.StayFile
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.UnavailablePeriod
import kotlinx.collections.immutable.toPersistentList

fun StayCommandModel.toEntity(): AppResultCompound<Stay, DomainError> {
    val files = files
        .map { file -> file.toEntity() }
        .toPersistentList()

    return Stay.parse(
        id = id,
        createDateTime = createDateTime,
        propertyId = propertyId,
        tenantId = tenantId,
        vehicleMake = vehicleMake,
        vehicleColor = vehicleColor,
        vehicleLicensePlate = vehicleLicensePlate,
        guests = guests,
        checkInDateTime = checkInDateTime,
        checkInTime = checkInTime,
        checkOutDateTime = checkOutDateTime,
        checkOutTime = checkOutTime,
        isAutoCheckIn = isAutoCheckIn,
        isAutoCheckOut = isAutoCheckOut,
        totalPrice = totalPrice,
        commission = commission,
        files = files,
        observations = observations,
    )
}

private fun StayFileCommandModel.toEntity() = StayFile(
    id = id,
    uriString = uriString,
    name = name,
)

fun UnavailablePeriodCommandModel.toEntity() = UnavailablePeriod.parse(
    id = id,
    createDateTime = createDateTime,
    propertyId = propertyId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    observations = observations,
)
