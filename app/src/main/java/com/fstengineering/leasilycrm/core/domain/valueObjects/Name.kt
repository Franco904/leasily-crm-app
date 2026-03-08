package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class Name private constructor(val value: String) {
    companion object {
        fun parse(name: String): AppResult<Name, DomainError.Name> {
            if (name.length > 100) {
                return AppResult.Error(DomainError.Name.LengthIsGreaterThan100Chars)
            }

            return AppResult.Success(Name(name))
        }
    }
}
