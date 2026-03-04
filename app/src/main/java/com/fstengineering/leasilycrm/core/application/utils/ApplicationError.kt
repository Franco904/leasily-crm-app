package com.fstengineering.leasilycrm.core.application.utils

import com.fstengineering.leasilycrm.core.domain.utils.SystemError

enum class ApplicationError : SystemError {
    NoInternet,
    Timeout,
    Unauthorized,
    Inconsistency,
    Unknown,
}
