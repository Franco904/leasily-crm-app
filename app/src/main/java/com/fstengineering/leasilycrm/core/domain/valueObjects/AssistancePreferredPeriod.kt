package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf

class AssistancePreferredPeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    companion object {
        fun parse(
            startDate: AppDate?,
            endDate: AppDate?,
        ): AppResultCompound<AssistancePreferredPeriod, DomainError.Assistance> {
            val errors = persistentListOf<DomainError.Assistance>()

            when {
                startDate == null || endDate == null -> {
                    errors.add(DomainError.Assistance.PreferredPeriodRequired)
                }

                else -> {
                    if (startDate == endDate) errors.add(DomainError.Assistance.PreferredPeriodStartAndEndDatesAreTheSame)
                    if (startDate > endDate) errors.add(DomainError.Assistance.PreferredPeriodStartDateIsAfterEndDate)
                }
            }

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors)

            val parsed = AssistancePreferredPeriod(
                startDate = requireNotNull(startDate),
                endDate = requireNotNull(endDate),
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
