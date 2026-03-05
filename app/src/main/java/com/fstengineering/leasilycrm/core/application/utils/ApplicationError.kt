package com.fstengineering.leasilycrm.core.application.utils

import com.fstengineering.leasilycrm.core.domain.utils.SystemError

sealed interface ApplicationError : SystemError {
    enum class External : ApplicationError {
        NoInternet,
        Timeout,
        Unauthorized,
        Inconsistency,
        Unknown,
    }
}
