package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayFileCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.StayFile
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.UnavailablePeriod
import kotlinx.collections.immutable.toPersistentList

fun StayCommandModel.toEntity(): AppResult<Stay, ValidationErrors> {
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
