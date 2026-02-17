package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceSearchedPeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    companion object {
        fun parse(
            startDate: AppDate?,
            endDate: AppDate?,
        ): AssistanceSearchedPeriod {
            validate(startDate, endDate)
                .onError { throw it.asException() }

            return AssistanceSearchedPeriod(
                startDate = requireNotNull(startDate),
                endDate = requireNotNull(endDate),
            )
        }

        fun validate(
            startDate: AppDate?,
            endDate: AppDate?,
        ): AppResult<Unit, DomainError.Assistance> {
            return when {
                startDate == null || endDate == null -> AppResult.Error(DomainError.Assistance.SearchingPeriodRequired)
                startDate == endDate -> AppResult.Error(DomainError.Assistance.SearchingPeriodStartAndEndDatesAreTheSame)
                startDate > endDate -> AppResult.Error(DomainError.Assistance.SearchingPeriodStartDateIsAfterEndDate)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
