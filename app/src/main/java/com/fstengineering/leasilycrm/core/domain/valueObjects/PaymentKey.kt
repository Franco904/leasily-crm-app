package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class PaymentKey private constructor(val value: String) {
    companion object {
        fun parse(paymentKey: String): AppResult<PaymentKey, DomainError.PaymentKey> {
            if (paymentKey.length > 120) {
                return AppResult.Error(DomainError.PaymentKey.LengthIsGreaterThan120Chars)
            }

            return AppResult.Success(PaymentKey(paymentKey))
        }
    }
}
