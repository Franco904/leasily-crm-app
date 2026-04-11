package com.fstengineering.leasilycrm.propertyDashboard.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyOwnerUiState
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedDateReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDashboard.presentation.models.PropertyAddressUiState
import com.fstengineering.leasilycrm.propertyDashboard.presentation.models.PropertyOccupiedDateUiState
import com.fstengineering.leasilycrm.propertyDashboard.presentation.models.PropertyUiState
import kotlinx.collections.immutable.toPersistentList

fun PropertyReadModel.toUiState(): PropertyUiState.Loaded {
    val (currentYearMonth, occupiedPeriods) = currentMonthOccupiedPeriods

    return PropertyUiState.Loaded(
        id = id,
        mainPicture = mainPictureUriString?.let {
            PropertyFileUiState(uri = it.toUri(), isMainPicture = true)
        },
        type = type,
        address = PropertyAddressUiState(
            neighborhood = neighborhood,
            streetAddress = streetAddress,
            number = number,
        ),
        owner = owner.toUiState(),
        currentMonthOccupiedPeriods = currentYearMonth to occupiedPeriods
            .map { it.toUiState() }
            .toPersistentList(),
    )
}

fun PropertyOccupiedDateReadModel.toUiState() = PropertyOccupiedDateUiState(
    dayOfMonth = date.dayOfMonth.toString(),
    firstHalfPeriodIndex = firstHalfPeriodIndex,
    secondHalfPeriodIndex = secondHalfPeriodIndex,
    periodId = periodId,
    tenantName = tenantName,
)

private fun PropertyOwnerReadModel.toUiState() = PropertyOwnerUiState(
    id = id,
    name = name,
)
