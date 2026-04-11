package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class Amount private constructor(val value: AppDecimal) {
    companion object {
        fun parse(value: AppDecimal): AppResult<Amount, DomainError> {
            return if (value.isNegative) AppResult.Error(DomainError.Amount.IsNegative)
            else AppResult.Success(Amount(value))
        }
    }
}
