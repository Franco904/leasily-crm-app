package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCalendarOccupiedDateUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.StayFileReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models.OccupiedPeriodDetailsUiState
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models.StayFileUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun OccupiedPeriodReadModel.toUiState() = OccupiedPeriodDetailsUiState.Loaded(
    id = id,
    tenantId = tenantId,
    property = property.toUiState(),
    tenant = tenant?.toUiState(),
    vehicleMake = vehicleMake,
    vehicleColor = vehicleColor,
    vehicleLicensePlate = vehicleLicensePlate,
    guests = guests,
    checkInDateTime = checkInDateTime,
    checkInHourMinute = checkInHourMinute,
    checkOutDateTime = checkOutDateTime,
    checkOutHourMinute = checkOutHourMinute,
    isAutoCheckIn = isAutoCheckIn,
    isAutoCheckOut = isAutoCheckOut,
    totalPrice = totalPrice,
    commission = commission,
    observations = observations,
    isCheckInDone = isCheckInDone,
    isCheckOutDone = isCheckOutDone,
    files = files.map { it.toUiState() }.toPersistentList(),
    transfers = persistentListOf(),
    occupiedDatesGrouped = occupiedDatesGrouped
        .mapValues { (_, dates) ->
            dates.map { date ->
                PropertyCalendarOccupiedDateUiState(
                    dayOfMonth = date.date.dayOfMonth.toString(),
                    firstHalfPeriodIndex = date.firstHalfPeriodIndex,
                    secondHalfPeriodIndex = date.secondHalfPeriodIndex,
                    periodId = date.periodId,
                )
            }.toPersistentList()
        }
        .toPersistentMap(),
)

fun PropertyReadModel.toUiState(): PropertyCardUiState {
    val numberText = if (number != null) ", $number" else ""

    return PropertyCardUiState(
        id = id,
        mainPictureUri = mainPictureUriString?.toUri(),
        neighborhood = neighborhood,
        streetAddressAndNumber = "$streetAddress$numberText",
        type = type,
    )
}

fun TenantReadModel.toUiState() = LeadTenantUiState(
    id = id,
    name = name,
    city = city,
)

fun StayFileReadModel.toUiState() = StayFileUiState(
    uri = uriString.toUri(),
    name = name,
)
