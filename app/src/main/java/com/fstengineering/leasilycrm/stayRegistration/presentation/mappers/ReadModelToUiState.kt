package com.fstengineering.leasilycrm.stayRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyUiState
import com.fstengineering.leasilycrm.stayRegistration.application.models.PropertyReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayFileReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.TenantReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodReadModel
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayDraftFormUiState
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayFileUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun StayReadModel.toUiState() = StayDraftFormUiState.Loaded.OwnedStay(
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

fun UnavailablePeriodReadModel.toUiState() = StayDraftFormUiState.Loaded.UnavailablePeriod(
    id = id,
    createDateTime = createDateTime,
    property = property?.toUiState(),
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    observations = observations,
)

fun PropertyReadModel.toUiState(): PropertyUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return PropertyUiState(
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
