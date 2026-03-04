package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionCommandModel
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.presentation.models.AssistanceInteractionBottomSheetDetailsUiState
import com.fstengineering.leasilycrm.core.presentation.models.AssistanceInteractionCardDetailsState

sealed interface AssistanceInteractionDraftFormUiState {
    data object Loading : AssistanceInteractionDraftFormUiState

    data class Loaded(
        val id: Long?,
        val date: AppDate,
        val title: String,
        val description: String?,
    ) : AssistanceInteractionDraftFormUiState {
        fun toCommand() = AssistanceInteractionCommandModel(
            id = id,
            date = date,
            title = title,
            description = description,
        )

        fun toCardDetails() = AssistanceInteractionCardDetailsState(
            date = date,
            title = title,
        )

        fun toBottomSheetDetails() = AssistanceInteractionBottomSheetDetailsUiState(
            date = date,
            title = title,
            description = description,
        )

        companion object {
            fun fromDomain(storedInteraction: AssistanceInteraction): Loaded {
                return Loaded(
                    id = storedInteraction.id,
                    date = storedInteraction.date,
                    title = storedInteraction.title,
                    description = storedInteraction.description,
                )
            }
        }
    }
}
