package com.fstengineering.leasilycrm.propertyRegistration.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

data class PropertyPricesUiState(
    val pricesByMonthYear: ImmutableMap<PriceMonthYearUiState, ImmutableList<PropertyPriceUiState>>
) {
    fun updatePrices(
        newPrices: ImmutableList<PropertyPriceUiState>,
    ): PropertyPricesUiState {
        val pricesByMonthYear = newPrices
            .sortedBy { it.startDate }
            .groupBy {
                PriceMonthYearUiState(
                    month = it.startDate.month,
                    year = it.startDate.year,
                )
            }
            .mapValues { (_, prices) -> prices.toPersistentList() }
            .toPersistentMap()

        return PropertyPricesUiState(pricesByMonthYear)
    }
}
