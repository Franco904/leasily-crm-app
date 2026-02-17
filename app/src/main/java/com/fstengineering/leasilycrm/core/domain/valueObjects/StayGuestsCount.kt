package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class StayGuestsCount private constructor(val count: Int) {
    companion object {
        fun parse(count: Int?): StayGuestsCount {
            validate(count)
                .onError { throw it.asException() }

            return StayGuestsCount(requireNotNull(count))
        }

        fun validate(guestsCount: Int?): AppResult<Unit, DomainError.Stay> {
            return when {
                guestsCount == null -> AppResult.Error(DomainError.Stay.GuestsCountRequired)
                guestsCount < 0 -> AppResult.Error(DomainError.Stay.GuestsCountIsNegative)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
