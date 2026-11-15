package com.fstengineering.leasilycrm.imovelDashboard.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import kotlinx.collections.immutable.ImmutableList

sealed interface ImovelUiState {
    data object Loading : ImovelUiState

    data class Loaded(
        val id: Long,
        val mainPicture: ImovelFileUiState?,
        val type: ImovelType,
        val address: ImovelAddressUiState,
        val proprietario: ProprietarioUiState,
        val currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<ImovelOccupiedDateUiState>>,
    ) : ImovelUiState {
        fun toCardDetails(): ImovelCardUiState {
            val numberText = if (address.number != null) ", ${address.number}" else ""
            val streetAddressAndNumber = "${address.streetAddress}$numberText"

            return ImovelCardUiState(
                id = id,
                mainPictureUri = mainPicture?.uri,
                neighborhood = address.neighborhood,
                streetAddressAndNumber = streetAddressAndNumber,
                type = type,
            )
        }
    }
}
