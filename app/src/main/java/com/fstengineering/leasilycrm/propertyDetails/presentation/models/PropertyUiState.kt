package com.fstengineering.leasilycrm.propertyDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface PropertyUiState {
    data object Loading : PropertyUiState

    data class Loaded(
        val id: Long,
        val ownerName: String,
        val type: PropertyType,
        val address: PropertyAddressUiState,
        val quantityCharacteristics: PropertyQuantityCharacteristicsUiState,
        val seaProximityTypeId: Long?,
        val distanceToSea: AppDecimal?,
        val includedItems: String?,
        val hasIncludedItems: Boolean?,
        val characteristics: ImmutableList<PropertyCharacteristic>,
        val cleaningServiceTax: AppDecimal?,
        val hasCleaningServiceIncluded: Boolean?,
        val prices: ImmutableMap<PriceMonthYearUiState, ImmutableList<PropertyPriceUiState>>,
        val files: ImmutableList<PropertyFileUiState>,
        val observations: String?,
    ) : PropertyUiState
}
