package com.fstengineering.leasilycrm.core.domain.utils

sealed interface ValidationError : Error {
    enum class UserName : ValidationError {
        IsBlank,
        LengthIsGreaterThan75Chars,
    }

    enum class CreciNumber : ValidationError {
        LengthIsGreaterThan8Chars,
    }
}
