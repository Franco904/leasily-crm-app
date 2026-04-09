package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal

data class PropertyPriceUiState(
    val id: Long? = null,
    val startDate: AppDate,
    val endDate: AppDate,
    val amount: AppDecimal,
)
