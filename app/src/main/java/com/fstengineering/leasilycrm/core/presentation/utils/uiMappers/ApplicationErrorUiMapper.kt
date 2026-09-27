package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel

fun ApplicationError.toBottomSheetData(
    properties: ErrorBottomSheetDataPropertiesStrategy,
): ErrorBottomSheetData =
    when (this) {
        ApplicationError.NoInternet -> {
            ErrorBottomSheetData(
                title = properties.noInternetErrorTitle,
                description = properties.noInternetErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Timeout -> {
            ErrorBottomSheetData(
                title = properties.timeOutErrorTitle,
                description = properties.timeOutErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Unauthorized -> {
            ErrorBottomSheetData(
                title = properties.unauthorizedErrorTitle,
                description = properties.unauthorizedErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

//        ApplicationError.Infra.Unknown -> {
//            ErrorBottomSheetData(
//                title = properties.inconsistencyErrorTitle,
//                description = properties.inconsistencyErrorDescription,
//                secondaryActionLabel = R.string.contact_support_action_button_text,
//                onSecondaryAction = { onErrorEvent ->
//                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
//                },
//            )
//        }

        else -> {
            ErrorBottomSheetData(
                title = properties.unknownErrorTitle,
                description = properties.unknownErrorDescription,
                secondaryActionLabel = R.string.contact_support_action_button_text,
                onSecondaryAction = { onErrorEvent ->
                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
                },
            )
        }
    }
