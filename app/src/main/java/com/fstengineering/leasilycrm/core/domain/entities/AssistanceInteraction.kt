package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceInteraction private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val date: AppDate,
    val title: String,
    val description: String?,
    val isDeleted: Boolean,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            date: AppDate?,
            title: String?,
            description: String?,
            isDeleted: Boolean,
        ): AssistanceInteraction {
            validateDate(date).onError { throw it.asException() }
            validateTitle(title).onError { throw it.asException() }

            return AssistanceInteraction(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                date = requireNotNull(date),
                title = requireNotNull(title),
                description = description,
                isDeleted = isDeleted,
            )
        }

        fun validateDate(date: AppDate?): AppResult<Unit, DomainError.Assistance> {
            return when {
                date == null -> AppResult.Error(DomainError.Assistance.InteractionDateRequired)
                else -> AppResult.Success(Unit)
            }
        }

        fun validateTitle(title: String?): AppResult<Unit, DomainError.Assistance> {
            return when {
                title.isNullOrBlank() -> AppResult.Error(DomainError.Assistance.InteractionTitleRequired)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
