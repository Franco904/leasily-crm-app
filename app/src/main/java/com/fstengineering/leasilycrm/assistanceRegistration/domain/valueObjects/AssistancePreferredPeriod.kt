package com.fstengineering.leasilycrm.assistanceRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.toPersistentList

class AssistancePreferredPeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    companion object {
        fun parse(
            startDate: AppDate,
            endDate: AppDate,
        ): AppResultCompound<AssistancePreferredPeriod, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (startDate == endDate) errors.add(DomainError.AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame)
            if (startDate > endDate) errors.add(DomainError.AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = AssistancePreferredPeriod(
                startDate = startDate,
                endDate = endDate,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
