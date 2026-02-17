package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class Phone private constructor(val value: String) {
    companion object {
        fun parse(phone: String): Phone {
            validate(phone)
                .onError { throw it.asException() }

            return Phone(requireNotNull(phone))
        }

        fun validate(phone: String): AppResult<Unit, ParsingError> {
            return when {
                phone.length > 20 -> AppResult.Error(ParsingError.PropertyOwner.PhoneLengthIsGreaterThan20Chars)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
