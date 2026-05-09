package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.mappers

import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayFileCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodFormUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.StayFileUiState
import kotlinx.collections.immutable.toPersistentList

fun OccupiedPeriodFormUiState.Loaded.Stay.toCommandModel(): StayCommandModel {
    return StayCommandModel(
        id = id,
        createDateTime = createDateTime,
        propertyId = property?.id,
        tenantId = tenant?.id,
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
        files = files.map { it.toCommandModel() }.toPersistentList(),
        observations = observations,
    )
}

fun OccupiedPeriodFormUiState.Loaded.UnavailablePeriod.toCommandModel() =
    UnavailablePeriodCommandModel(
        id = id,
        createDateTime = createDateTime,
        propertyId = property?.id,
        checkInDateTime = checkInDateTime,
        checkOutDateTime = checkOutDateTime,
        observations = observations,
    )

fun StayFileUiState.toCommandModel() = StayFileCommandModel(
    id = id,
    uriString = uri.toString(),
    name = name,
)
