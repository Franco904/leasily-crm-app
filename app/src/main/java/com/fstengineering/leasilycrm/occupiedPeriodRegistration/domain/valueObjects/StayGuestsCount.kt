package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class StayGuestsCount private constructor(val count: Int) {
    companion object {
        fun parse(count: Int): AppResult<StayGuestsCount, DomainError.StayOtherFields> {
            if (count < 0) return AppResult.Error(DomainError.StayOtherFields.GuestsCountIsNegative)

            return AppResult.Success(data = StayGuestsCount(count))
        }
    }
}
