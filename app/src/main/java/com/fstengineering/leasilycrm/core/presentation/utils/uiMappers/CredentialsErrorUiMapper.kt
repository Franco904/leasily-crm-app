package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.infra.utils.CredentialsError
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel
import com.fstengineering.leasilycrm.core.presentation.utils.openAddGoogleAccountSettingsApp

fun Exception.toCredentialsError() = when (this) {
    is GetCredentialCancellationException -> CredentialsError.UserCancellation
    is NoCredentialException -> CredentialsError.NoCredentialsFound
    else -> CredentialsError.UnknownError
}

fun CredentialsError.toBottomSheetData() = when (this) {
    CredentialsError.UserCancellation ->
        ErrorBottomSheetData(
            title = -1,
            description = -1,
            secondaryActionLabel = null,
            onSecondaryAction = null,
        )

    CredentialsError.NoCredentialsFound ->
        ErrorBottomSheetData(
            title = R.string.no_credentials_found_error_title,
            description = R.string.no_credentials_found_error_description,
            secondaryActionLabel = R.string.register_account_action_button_text,
            onSecondaryAction = { onErrorEvent ->
                openAddGoogleAccountSettingsApp { errorUiEvent -> onErrorEvent(errorUiEvent) }
            },
        )

    CredentialsError.UnknownError ->
        ErrorBottomSheetData(
            title = R.string.unknown_error_title,
            description = R.string.unknown_error_description_sign_in,
            secondaryActionLabel = R.string.contact_support_action_button_text,
            onSecondaryAction = { onErrorEvent ->
                mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
            },
        )
}
