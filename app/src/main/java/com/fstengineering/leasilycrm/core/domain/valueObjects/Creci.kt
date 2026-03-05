package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf

class Creci private constructor(val value: String) {
    companion object {
        fun parse(creciNumber: String): AppResultCompound<Creci, DomainError.UserCreci> {
            if (creciNumber.length > 8) {
                val error = DomainError.UserCreci.LengthIsGreaterThan8Chars
                return AppResultCompound.Errors(errors = persistentListOf(error))
            }

            return AppResultCompound.Success(data = Creci(creciNumber))
        }
    }
}
