package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError

enum class InfraError : BaseError {
    NoInternet,
    Timeout,
    Unauthorized,
    Unknown;

    fun toApplicationError(): ApplicationError = when (this) {
        NoInternet -> ApplicationError.Infra.NoInternet
        Timeout -> ApplicationError.Infra.Timeout
        Unauthorized -> ApplicationError.Infra.Unauthorized
        Unknown -> ApplicationError.Infra.Unknown
    }
}
