package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class Creci private constructor(val value: String) {
    companion object {
        fun parse(creciNumber: String): Creci {
            validate(creciNumber)
                .onError { throw it.asException() }

            return Creci(creciNumber)
        }

        fun validate(creciNumber: String): AppResult<Unit, DomainError.User> {
            return when {
                creciNumber.length > 8 -> AppResult.Error(DomainError.User.CreciNumberLengthIsGreaterThan8Chars)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
