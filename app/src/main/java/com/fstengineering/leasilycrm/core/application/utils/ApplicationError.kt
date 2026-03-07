package com.fstengineering.leasilycrm.core.application.utils

import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError

enum class ApplicationError : BusinessRuleError {
    NoInternet,
    Timeout,
    Unauthorized,
    Unknown,
}
