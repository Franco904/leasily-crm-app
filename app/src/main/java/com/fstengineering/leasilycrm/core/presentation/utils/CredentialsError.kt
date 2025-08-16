package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.core.domain.utils.Error

enum class CredentialsError : Error {
    UserCancellation,
    UnsupportedCrendentialType,
    UnsupportedApi,
    NoCredentialsFound,
    UnknownError,
}
