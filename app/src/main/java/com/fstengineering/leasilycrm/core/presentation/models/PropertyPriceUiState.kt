package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

data class PropertyPriceUiState(
    val id: Long? = null,
    val startDate: AppDate,
    val endDate: AppDate,
    val amount: AppDecimal,
)
