package com.fstengineering.leasilycrm.profile.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class Creci private constructor(val value: String) {
    companion object {
        fun parse(creciNumber: String): AppResult<Creci, DomainError.UserCreci> {
            if (creciNumber.length > 8) {
                return AppResult.Error(error = DomainError.UserCreci.LengthIsGreaterThan8Chars)
            }

            return AppResult.Success(data = Creci(creciNumber))
        }
    }
}