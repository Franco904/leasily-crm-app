package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf
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
        ): AppResultCompound<AssistanceInteraction, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (date == null) errors.add(DomainError.AssistanceInteractionDate.Required)
            if (title.isNullOrBlank()) errors.add(DomainError.AssistanceInteractionTitle.Required)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = AssistanceInteraction(
                id = id,
                date = requireNotNull(date),
                title = requireNotNull(title),
                description = description,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
