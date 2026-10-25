package com.fstengineering.leasilycrm.propertyDashboard.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import kotlinx.collections.immutable.ImmutableList

sealed interface PropertyUiState {
    data object Loading : PropertyUiState

    data class Loaded(
        val id: Long,
        val mainPicture: PropertyFileUiState?,
        val type: PropertyType,
        val address: PropertyAddressUiState,
        val owner: ProprietarioUiState,
        val currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<PropertyOccupiedDateUiState>>,
    ) : PropertyUiState {
        fun toCardDetails(): PropertyCardUiState {
            val numberText = if (address.number != null) ", ${address.number}" else ""
            val streetAddressAndNumber = "${address.streetAddress}$numberText"

            return PropertyCardUiState(
                id = id,
                mainPictureUri = mainPicture?.uri,
                neighborhood = address.neighborhood,
                streetAddressAndNumber = streetAddressAndNumber,
                type = type,
            )
        }
    }
}
