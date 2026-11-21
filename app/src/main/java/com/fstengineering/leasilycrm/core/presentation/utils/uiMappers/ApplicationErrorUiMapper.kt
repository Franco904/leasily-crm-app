package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.CredentialsError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel

/**
 * Dados do bottom sheet de qualquer [BaseError]. Erro de domínio ou de validação não chega à
 * tela como falha de infra: a tela já o trata no formulário, e se chegar mostra o erro genérico.
 */
fun BaseError.toBottomSheetData(
    imoveis: ErrorBottomSheetDataPropertiesStrategy,
): ErrorBottomSheetData = when (this) {
    is ApplicationError -> toBottomSheetData(imoveis)
    is CredentialsError -> toBottomSheetData()
    is DomainError, is ValidationErrors -> ApplicationError.Unknown.toBottomSheetData(imoveis)
}

fun ApplicationError.toBottomSheetData(
    imoveis: ErrorBottomSheetDataPropertiesStrategy,
): ErrorBottomSheetData =
    when (this) {
        ApplicationError.NoInternet -> {
            ErrorBottomSheetData(
                title = imoveis.noInternetErrorTitle,
                description = imoveis.noInternetErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Timeout -> {
            ErrorBottomSheetData(
                title = imoveis.timeOutErrorTitle,
                description = imoveis.timeOutErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Unauthorized -> {
            ErrorBottomSheetData(
                title = imoveis.unauthorizedErrorTitle,
                description = imoveis.unauthorizedErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

//        ApplicationError.Infra.Unknown -> {
//            ErrorBottomSheetData(
//                title = imoveis.inconsistencyErrorTitle,
//                description = imoveis.inconsistencyErrorDescription,
//                secondaryActionLabel = R.string.contact_support_action_button_text,
//                onSecondaryAction = { onErrorEvent ->
//                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
//                },
//            )
//        }

        else -> {
            ErrorBottomSheetData(
                title = imoveis.unknownErrorTitle,
                description = imoveis.unknownErrorDescription,
                secondaryActionLabel = R.string.contact_support_action_button_text,
                onSecondaryAction = { onErrorEvent ->
                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
                },
            )
        }
    }
