package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel

fun ApplicationError.External.toBottomSheetData(
    properties: ErrorBottomSheetDataPropertiesStrategy,
): ErrorBottomSheetData =
    when (this) {
        ApplicationError.External.NoInternet -> {
            ErrorBottomSheetData(
                title = properties.noInternetErrorTitle,
                description = properties.noInternetErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.External.Timeout -> {
            ErrorBottomSheetData(
                title = properties.timeOutErrorTitle,
                description = properties.timeOutErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.External.Unauthorized -> {
            ErrorBottomSheetData(
                title = properties.unauthorizedErrorTitle,
                description = properties.unauthorizedErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.External.Inconsistency -> {
            ErrorBottomSheetData(
                title = properties.inconsistencyErrorTitle,
                description = properties.inconsistencyErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.External.Unknown -> {
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
