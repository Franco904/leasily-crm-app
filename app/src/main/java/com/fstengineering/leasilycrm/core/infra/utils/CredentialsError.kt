package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.domain.utils.BaseError

enum class CredentialsError : BaseError {
    UserCancellation,
    NoCredentialsFound,
    UnknownError,
}
