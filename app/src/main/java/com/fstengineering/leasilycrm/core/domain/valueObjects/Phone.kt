package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class Phone private constructor(val value: String) {
    companion object {
        fun parse(phone: String): Phone {
            validate(phone)
                .onError { throw it.asException() }

            return Phone(phone)
        }

        fun validate(phone: String): AppResult<Unit, DomainError> {
            return when {
                phone.length > 20 -> AppResult.Error(DomainError.PropertyOwner.PhoneLengthIsGreaterThan20Chars)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
