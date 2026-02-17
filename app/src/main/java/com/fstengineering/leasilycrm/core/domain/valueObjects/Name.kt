package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class Name private constructor(val value: String) {
    companion object {
        fun parse(name: String?): Name {
            validate(name)
                .onError { throw it.asException() }

            return Name(requireNotNull(name))
        }

        fun validate(name: String?): AppResult<Unit, ParsingError> {
            return when {
                name.isNullOrBlank() -> AppResult.Error(ParsingError.PropertyOwner.NameRequired)
                name.length > 100 -> AppResult.Error(ParsingError.PropertyOwner.NameLengthIsGreaterThan100Chars)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
