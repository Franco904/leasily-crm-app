package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf

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
        ): AppResultCompound<AssistanceInteraction, DomainError.Assistance> {
            val errors = persistentListOf<DomainError.Assistance>()

            if (date == null) errors.add(DomainError.Assistance.InteractionDateRequired)
            if (title.isNullOrBlank()) errors.add(DomainError.Assistance.InteractionTitleRequired)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors)

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
