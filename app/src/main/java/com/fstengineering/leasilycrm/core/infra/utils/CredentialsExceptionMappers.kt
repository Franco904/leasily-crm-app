package com.fstengineering.leasilycrm.core.infra.utils

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

fun Exception.toApplicationError() = when (this) {
    is GetCredentialCancellationException -> ApplicationError.UserCancellation
    is NoCredentialException -> ApplicationError.NoCredentialsFound
    else -> ApplicationError.Unknown
}
