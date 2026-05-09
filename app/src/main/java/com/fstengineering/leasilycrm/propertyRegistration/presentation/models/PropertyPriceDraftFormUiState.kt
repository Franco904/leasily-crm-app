package com.fstengineering.leasilycrm.propertyRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState

data class PropertyPriceDraftFormUiState(
    val id: Long? = null,
    val startDate: AppDate? = null,
    val endDate: AppDate? = null,
    val amount: AppDecimal? = null,
) {
    fun toSavedUiState() = PropertyPriceUiState(
        id = id,
        startDate = requireNotNull(startDate),
        endDate = requireNotNull(endDate),
        amount = requireNotNull(amount),
    )

    companion object {
        fun fromSavedUiState(saved: PropertyPriceUiState) = PropertyPriceDraftFormUiState(
            id = saved.id,
            startDate = saved.startDate,
            endDate = saved.endDate,
            amount = saved.amount,
        )
    }
}
