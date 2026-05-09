package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class Phone private constructor(val value: String) {
    companion object {
        fun parse(phone: String): AppResult<Phone, DomainError.Phone> {
            if (phone.length > 20) {
                return AppResult.Error(DomainError.Phone.LengthIsGreaterThan20Chars)
            }

            return AppResult.Success(Phone(phone))
        }
    }
}
