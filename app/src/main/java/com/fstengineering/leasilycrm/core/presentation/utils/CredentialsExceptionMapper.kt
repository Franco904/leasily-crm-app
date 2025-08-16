package com.fstengineering.leasilycrm.core.presentation.utils

import androidx.credentials.exceptions.ClearCredentialUnsupportedException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException

fun Exception.toCredentialsError() = when (this) {
    is GetCredentialCancellationException -> CredentialsError.UserCancellation

    is GetCredentialUnsupportedException, is ClearCredentialUnsupportedException -> {
        CredentialsError.UnsupportedApi
    }

    is NoCredentialException -> CredentialsError.NoCredentialsFound
    else -> CredentialsError.UnknownError
}
