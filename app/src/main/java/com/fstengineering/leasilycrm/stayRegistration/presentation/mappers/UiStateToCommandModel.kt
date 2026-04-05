package com.fstengineering.leasilycrm.stayRegistration.presentation.mappers

import com.fstengineering.leasilycrm.stayRegistration.application.models.StayCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayFileCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayDraftFormUiState
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayFileUiState
import kotlinx.collections.immutable.toPersistentList

fun StayDraftFormUiState.Loaded.OwnedStay.toCommandModel(): StayCommandModel {
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

fun StayDraftFormUiState.Loaded.UnavailablePeriod.toCommandModel() = UnavailablePeriodCommandModel(
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
