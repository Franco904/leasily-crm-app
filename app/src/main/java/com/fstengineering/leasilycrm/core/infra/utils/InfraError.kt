package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError

enum class InfraError : BaseError {
    NoInternet,
    Timeout,
    Unauthorized,
    Unknown;

    fun toApplication(): ApplicationError = when (this) {
        NoInternet -> ApplicationError.NoInternet
        Timeout -> ApplicationError.Timeout
        Unauthorized -> ApplicationError.Unauthorized
        Unknown -> ApplicationError.Unknown
    }
}
