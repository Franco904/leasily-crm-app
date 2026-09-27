package com.fstengineering.leasilycrm.assistanceRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import kotlinx.collections.immutable.toPersistentList

class AssistanceInteraction private constructor(
    val id: Long?,
    val date: AppDate,
    val title: String,
    val description: String?,
) {
    companion object {
        fun parse(
            id: Long?,
            date: AppDate?,
            title: String?,
            description: String?,
        ): AppResult<AssistanceInteraction, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (date == null) errors.add(DomainError.AssistanceInteractionDate.Required)
            if (title.isNullOrBlank()) errors.add(DomainError.AssistanceInteractionTitle.Required)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = AssistanceInteraction(
                id = id,
                date = requireNotNull(date),
                title = requireNotNull(title),
                description = description,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
