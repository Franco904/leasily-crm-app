package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class AssistancePreferredPeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    companion object {
        fun parse(
            startDate: AppDate?,
            endDate: AppDate?,
        ): AppResultCompound<AssistancePreferredPeriod, DomainError> {
            val errors = mutableListOf<DomainError>()

            when {
                startDate == null || endDate == null -> {
                    errors.add(DomainError.AssistanceOtherFields.PreferredPeriodRequired)
                }

                else -> {
                    if (startDate == endDate) errors.add(DomainError.AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame)
                    if (startDate > endDate) errors.add(DomainError.AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate)
                }
            }

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = AssistancePreferredPeriod(
                startDate = requireNotNull(startDate),
                endDate = requireNotNull(endDate),
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
