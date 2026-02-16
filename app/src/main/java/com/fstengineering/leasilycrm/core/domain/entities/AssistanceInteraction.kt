package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateAssistanceInteractionDate
import com.fstengineering.leasilycrm.core.domain.validations.validateAssistanceInteractionTitle

class AssistanceInteraction private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val interactionDate: AppDate,
    val title: String,
    val description: String?,
    val isDeleted: Boolean,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            interactionDate: AppDate?,
            title: String?,
            description: String?,
            isDeleted: Boolean,
        ): AssistanceInteraction {
            validateAssistanceInteractionDate(interactionDate).onError { throw it.asException() }
            validateAssistanceInteractionTitle(title).onError { throw it.asException() }

            return AssistanceInteraction(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                interactionDate = requireNotNull(interactionDate),
                title = requireNotNull(title),
                description = description,
                isDeleted = isDeleted,
            )
        }
    }
}
