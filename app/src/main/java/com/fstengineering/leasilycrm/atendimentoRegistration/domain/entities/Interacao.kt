package com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import kotlinx.collections.immutable.toPersistentList

class Interacao private constructor(
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
        ): AppResult<Interacao> {
            val errors = mutableListOf<DomainError>()

            if (date == null) errors.add(DomainError.InteracaoDate.Required)
            if (title.isNullOrBlank()) errors.add(DomainError.InteracaoTitle.Required)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = Interacao(
                id = id,
                date = requireNotNull(date),
                title = requireNotNull(title),
                description = description,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
