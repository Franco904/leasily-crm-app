package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.utils.CredentialsError

fun CredentialsError.toUiMessage() = when (this) {
    CredentialsError.UserCancellation -> {
        R.string.snackbar_credential_user_cancellation_error
    }

    CredentialsError.UnsupportedCrendentialType -> {
        R.string.snackbar_credential_unsupported_type_error
    }

    CredentialsError.UnsupportedApi -> {
        R.string.snackbar_credential_unsupported_api_error
    }

    CredentialsError.NoCredentialsFound -> {
        R.string.snackbar_credential_no_credentials_found_error
    }

    CredentialsError.UnknownError -> {
        R.string.snackbar_unknown_error
    }
}
