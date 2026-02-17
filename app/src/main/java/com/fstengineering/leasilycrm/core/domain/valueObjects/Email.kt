package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class Email private constructor(val value: String) {
    companion object {
        private const val EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$"

        fun parse(email: String): Email {
            validate(email)
                .onError { throw it.asException() }

            return Email(requireNotNull(email))
        }

        fun validate(email: String): AppResult<Unit, DomainError> {
            return when {
                !email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE)) -> {
                    AppResult.Error(DomainError.PropertyOwner.EmailInvalidFormat)
                }

                email.length > 20 -> AppResult.Error(DomainError.PropertyOwner.EmailLengthIsGreaterThan120Chars)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
