package com.fstengineering.leasilycrm.leadTenantDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.presentation.models.AssistanceInteractionCardUiState

data class AssistanceInteractionUiState(
    val id: Long,
    val date: AppDate,
    val title: String,
    val description: String?,
) {
    fun toCardUiState() = AssistanceInteractionCardUiState(
        date = date,
        title = title,
    )
}
