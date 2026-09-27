package com.fstengineering.leasilycrm.assistanceRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import kotlinx.collections.immutable.toPersistentList

class AssistancePreferredPeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    companion object {
        fun parse(
            startDate: AppDate,
            endDate: AppDate,
        ): AppResult<AssistancePreferredPeriod, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (startDate == endDate) errors.add(DomainError.AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame)
            if (startDate > endDate) errors.add(DomainError.AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = AssistancePreferredPeriod(
                startDate = startDate,
                endDate = endDate,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
