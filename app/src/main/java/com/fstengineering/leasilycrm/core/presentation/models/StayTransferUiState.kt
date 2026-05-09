package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

data class StayTransferUiState(
    val id: Long?,
    val date: AppDate?,
    val transferAmount: AppDecimal?,
    val transferDescription: String?,
)
