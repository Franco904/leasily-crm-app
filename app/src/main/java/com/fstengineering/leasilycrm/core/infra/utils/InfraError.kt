package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError

enum class InfraError : BaseError {
    NoInternet,
    Timeout,
    Unauthorized,
    Unknown;

    fun toApplication(): ApplicationError = when (this) {
        NoInternet -> ApplicationError.External.NoInternet
        Timeout -> ApplicationError.External.Timeout
        Unauthorized -> ApplicationError.External.Unauthorized
        Unknown -> ApplicationError.External.Unknown
    }
}
