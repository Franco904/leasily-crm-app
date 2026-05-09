package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.toPersistentList

class Email private constructor(val value: String) {
    companion object {
        private const val EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$"

        fun parse(email: String): AppResultCompound<Email, DomainError.Email> {
            val errors = mutableListOf<DomainError.Email>()

            if (!email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE))) {
                errors.add(DomainError.Email.Invalid)
            }

            if (email.length > 120) {
                errors.add(DomainError.Email.LengthIsGreaterThan120Chars)
            }

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            return AppResultCompound.Success(Email(email))
        }
    }
}
