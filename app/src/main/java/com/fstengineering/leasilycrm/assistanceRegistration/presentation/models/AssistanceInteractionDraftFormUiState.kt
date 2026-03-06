package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionReadModel
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
            fun fromReadModel(readModel: AssistanceInteractionReadModel): Loaded {
                return Loaded(
                    id = readModel.id,
                    date = readModel.date,
                    title = readModel.title,
                    description = readModel.description,
                )
            }
        }
    }
}
