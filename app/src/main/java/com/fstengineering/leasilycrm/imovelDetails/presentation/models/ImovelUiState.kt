package com.fstengineering.leasilycrm.imovelDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface ImovelUiState {
    data object Loading : ImovelUiState

    data class Loaded(
        val id: Long,
        val type: ImovelType,
        val address: ImovelAddressUiState,
        val quantityCharacteristics: ImovelQuantityCharacteristicsUiState,
        val seaProximityTypeId: Long?,
        val distanceToSea: AppDecimal?,
        val includedItems: String?,
        val hasIncludedItems: Boolean?,
        val characteristics: ImmutableList<ImovelCharacteristic>,
        val cleaningServiceTax: AppDecimal?,
        val hasCleaningServiceIncluded: Boolean?,
        val prices: ImmutableMap<PriceMonthYearUiState, ImmutableList<ImovelPriceUiState>>,
        val files: ImmutableList<ImovelFileUiState>,
        val observations: String?,
    ) : ImovelUiState
}
