package com.fstengineering.leasilycrm.imovelRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState

data class ImovelPriceDraftFormUiState(
    val id: Long? = null,
    val startDate: AppDate? = null,
    val endDate: AppDate? = null,
    val amount: AppDecimal? = null,
) {
    fun toSavedUiState() = ImovelPriceUiState(
        id = id,
        startDate = requireNotNull(startDate),
        endDate = requireNotNull(endDate),
        amount = requireNotNull(amount),
    )

    companion object {
        fun fromSavedUiState(saved: ImovelPriceUiState) = ImovelPriceDraftFormUiState(
            id = saved.id,
            startDate = saved.startDate,
            endDate = saved.endDate,
            amount = saved.amount,
        )
    }
}
