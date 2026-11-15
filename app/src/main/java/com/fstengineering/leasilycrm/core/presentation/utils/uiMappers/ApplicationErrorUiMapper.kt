package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel

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
