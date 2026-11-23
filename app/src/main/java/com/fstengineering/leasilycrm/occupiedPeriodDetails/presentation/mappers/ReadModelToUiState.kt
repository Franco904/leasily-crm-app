package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.mappers

import com.fstengineering.leasilycrm.core.presentation.models.ClienteUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCalendarOccupiedDateUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.StayFileReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models.OccupiedPeriodDetailsUiState
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models.StayFileUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun OccupiedPeriodReadModel.toUiState() = OccupiedPeriodDetailsUiState.Loaded(
    id = id,
    tenantId = tenantId,
    imovel = imovel.toUiState(),
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
                ImovelCalendarOccupiedDateUiState(
                    dayOfMonth = date.date.dayOfMonth.toString(),
                    firstHalfPeriodIndex = date.firstHalfPeriodIndex,
                    secondHalfPeriodIndex = date.secondHalfPeriodIndex,
                    periodId = date.periodId,
                )
            }.toPersistentList()
        }
        .toPersistentMap(),
)

fun ImovelReadModel.toUiState(): ImovelCardUiState {
    val numberText = if (number != null) ", $number" else ""

    return ImovelCardUiState(
        id = id,
        mainPictureUriString = mainPictureUriString,
        neighborhood = neighborhood,
        streetAddressAndNumber = "$streetAddress$numberText",
        type = type,
    )
}

fun TenantReadModel.toUiState() = ClienteUiState(
    id = id,
    name = name,
    city = city,
)

fun StayFileReadModel.toUiState() = StayFileUiState(
    uriString = uriString,
    name = name,
)
