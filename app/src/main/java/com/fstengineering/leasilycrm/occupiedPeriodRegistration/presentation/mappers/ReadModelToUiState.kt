package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayFileReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodFormUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.StayFileUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun StayReadModel.toUiState() = OccupiedPeriodFormUiState.Loaded.Stay(
    id = id,
    createDateTime = createDateTime,
    property = property?.toUiState(),
    tenant = tenant?.toUiState(),
    vehicleMake = vehicleMake,
    vehicleColor = vehicleColor,
    vehicleLicensePlate = vehicleLicensePlate,
    guests = guests ?: 0,
    checkInDateTime = checkInDateTime,
    checkInTime = checkInDateTime?.hour to checkInDateTime?.minute,
    checkOutDateTime = checkOutDateTime,
    checkOutTime = checkOutDateTime?.hour to checkOutDateTime?.minute,
    isAutoCheckIn = isAutoCheckIn == true,
    isAutoCheckOut = isAutoCheckOut == true,
    totalPrice = totalPrice,
    commission = commission,
    files = files?.map { it.toUiState() }?.toPersistentList() ?: persistentListOf(),
    observations = observations,
)

fun UnavailablePeriodReadModel.toUiState() = OccupiedPeriodFormUiState.Loaded.UnavailablePeriod(
    id = id,
    createDateTime = createDateTime,
    property = property?.toUiState(),
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    observations = observations,
)

fun PropertyReadModel.toUiState(): PropertyCardUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return PropertyCardUiState(
        id = requireNotNull(id),
        mainPictureUri = mainPictureUriString?.toUri(),
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
        occupiedDates = occupiedDates,
    )
}

fun TenantReadModel.toUiState() = LeadTenantUiState(
    id = requireNotNull(id),
    name = name,
    city = city,
)

fun StayFileReadModel.toUiState() = StayFileUiState(
    id = id,
    uri = uriString.toUri(),
    name = name,
)
