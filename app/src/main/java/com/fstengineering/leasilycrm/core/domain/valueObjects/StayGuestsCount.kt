package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class StayGuestsCount private constructor(val count: Int) {
    companion object {
        fun parse(count: Int?): StayGuestsCount {
            validate(count)
                .onError { throw it.asException() }

            return StayGuestsCount(requireNotNull(count))
        }

        fun validate(guestsCount: Int?): AppResult<Unit, ParsingError.Stay> {
            return when {
                guestsCount == null -> AppResult.Error(ParsingError.Stay.GuestsCountRequired)
                guestsCount < 0 -> AppResult.Error(ParsingError.Stay.GuestsCountIsNegative)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
