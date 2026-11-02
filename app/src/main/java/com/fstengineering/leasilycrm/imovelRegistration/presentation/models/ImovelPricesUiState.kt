package com.fstengineering.leasilycrm.imovelRegistration.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

data class ImovelPricesUiState(
    val pricesByMonthYear: ImmutableMap<PriceMonthYearUiState, ImmutableList<ImovelPriceUiState>>
) {
    fun updatePrices(
        newPrices: ImmutableList<ImovelPriceUiState>,
    ): ImovelPricesUiState {
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

        return ImovelPricesUiState(pricesByMonthYear)
    }
}
