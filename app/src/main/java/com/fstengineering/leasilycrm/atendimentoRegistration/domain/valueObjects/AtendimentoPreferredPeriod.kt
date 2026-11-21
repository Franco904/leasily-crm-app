package com.fstengineering.leasilycrm.atendimentoRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import kotlinx.collections.immutable.toPersistentList

class AtendimentoPreferredPeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    companion object {
        fun parse(
            startDate: AppDate,
            endDate: AppDate,
        ): AppResult<AtendimentoPreferredPeriod> {
            val errors = mutableListOf<DomainError>()

            if (startDate == endDate) errors.add(DomainError.AtendimentoOtherFields.PreferredPeriodStartAndEndDatesAreTheSame)
            if (startDate > endDate) errors.add(DomainError.AtendimentoOtherFields.PreferredPeriodStartDateIsAfterEndDate)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = AtendimentoPreferredPeriod(
                startDate = startDate,
                endDate = endDate,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
