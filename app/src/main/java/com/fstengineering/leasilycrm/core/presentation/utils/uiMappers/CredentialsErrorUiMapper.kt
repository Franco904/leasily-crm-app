package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.utils.CredentialsError

fun Exception.toCredentialsError() = when (this) {
    is GetCredentialCancellationException -> CredentialsError.UserCancellation
    is NoCredentialException -> CredentialsError.NoCredentialsFound
    else -> CredentialsError.UnknownError
}

fun CredentialsError.toBottomSheetData() = when (this) {
    CredentialsError.UserCancellation ->
        ErrorBottomSheetData(
            title = R.string.user_cancellation_error_title,
            description = R.string.user_cancellation_error_title
        )

    CredentialsError.NoCredentialsFound ->
        ErrorBottomSheetData(
            title = R.string.no_credentials_found_error_title,
            description = R.string.no_credentials_found_error_description
        )

    CredentialsError.UnknownError ->
        ErrorBottomSheetData(
            title = R.string.unknown_error_title,
            description = R.string.unknown_error_description_sign_in
        )
}
