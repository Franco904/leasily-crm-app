package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.presentation.models.AssistanceInteractionBottomSheetUiState
import com.fstengineering.leasilycrm.core.presentation.models.AssistanceInteractionCardUiState

sealed interface AssistanceInteractionDraftFormUiState {
    data object Loading : AssistanceInteractionDraftFormUiState

    data class Loaded(
        val id: Long?,
        val date: AppDate,
        val title: String,
        val description: String?,
    ) : AssistanceInteractionDraftFormUiState {
        fun toCardUiState() = AssistanceInteractionCardUiState(
            date = date,
            title = title,
        )

        fun toBottomSheetUiState() = AssistanceInteractionBottomSheetUiState(
            date = date,
            title = title,
            description = description,
        )
    }
}
