package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetUiState
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel
import com.fstengineering.leasilycrm.core.presentation.utils.openAddGoogleAccountSettingsApp
import com.fstengineering.leasilycrm.core.presentation.utils.strategies.ErrorBottomSheetDataPropertiesStrategy

/**
 * Dados do bottom sheet de qualquer [BaseError]. Erro de domínio ou de validação não chega à
 * tela como falha de infra: a tela já o trata no formulário, e se chegar mostra o erro genérico.
 */
fun BaseError.toBottomSheetData(
    imoveis: ErrorBottomSheetDataPropertiesStrategy,
): ErrorBottomSheetUiState = when (this) {
    is ApplicationError -> toBottomSheetData(imoveis)
    is DomainError, is ValidationErrors -> ApplicationError.Unknown.toBottomSheetData(imoveis)
}

fun ApplicationError.toBottomSheetData(
    imoveis: ErrorBottomSheetDataPropertiesStrategy,
): ErrorBottomSheetUiState =
    when (this) {
        ApplicationError.NoInternet -> {
            ErrorBottomSheetUiState(
                title = imoveis.noInternetErrorTitle,
                description = imoveis.noInternetErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Timeout -> {
            ErrorBottomSheetUiState(
                title = imoveis.timeOutErrorTitle,
                description = imoveis.timeOutErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Unauthorized -> {
            ErrorBottomSheetUiState(
                title = imoveis.unauthorizedErrorTitle,
                description = imoveis.unauthorizedErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.UserCancellation -> {
            ErrorBottomSheetUiState(
                title = -1,
                description = -1,
                secondaryActionLabel = null,
                onSecondaryAction = null,
            )
        }

        ApplicationError.NoCredentialsFound -> {
            ErrorBottomSheetUiState(
                title = R.string.no_credentials_found_error_title,
                description = R.string.no_credentials_found_error_description,
                secondaryActionLabel = R.string.register_account_action_button_text,
                onSecondaryAction = { onErrorEvent ->
                    openAddGoogleAccountSettingsApp { errorUiEvent -> onErrorEvent(errorUiEvent) }
                },
            )
        }

        ApplicationError.Unknown -> {
            ErrorBottomSheetUiState(
                title = imoveis.unknownErrorTitle,
                description = imoveis.unknownErrorDescription,
                secondaryActionLabel = R.string.contact_support_action_button_text,
                onSecondaryAction = { onErrorEvent ->
                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
                },
            )
        }
    }
