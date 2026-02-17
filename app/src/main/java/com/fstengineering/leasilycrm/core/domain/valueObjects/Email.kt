package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.constants.EMAIL_REGEX
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class Email private constructor(val value: String) {
    companion object {
        fun parse(email: String): Email {
            validate(email)
                .onError { throw it.asException() }

            return Email(requireNotNull(email))
        }

        fun validate(email: String): AppResult<Unit, ParsingError> {
            return when {
                !email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE)) -> {
                    AppResult.Error(ParsingError.PropertyOwner.EmailInvalidFormat)
                }

                email.length > 20 -> AppResult.Error(ParsingError.PropertyOwner.EmailLengthIsGreaterThan120Chars)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
