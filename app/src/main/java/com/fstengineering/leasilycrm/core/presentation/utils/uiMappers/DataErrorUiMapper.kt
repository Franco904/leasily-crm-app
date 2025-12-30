package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel

fun DataError.toBottomSheetData(properties: ErrorBottomSheetDataPropertiesStrategy): ErrorBottomSheetData =
    when (this) {
        DataError.NoInternetError ->
            ErrorBottomSheetData(
                title = properties.noInternetErrorTitle,
                description = properties.noInternetErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )

        DataError.TimeOutError ->
            ErrorBottomSheetData(
                title = properties.timeOutErrorTitle,
                description = properties.timeOutErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )

        DataError.UnauthorizedError ->
            ErrorBottomSheetData(
                title = properties.unauthorizedErrorTitle,
                description = properties.unauthorizedErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )

        DataError.UnknownError ->
            ErrorBottomSheetData(
                title = properties.unknownErrorTitle,
                description = properties.unknownErrorDescription,
                secondaryActionLabel = R.string.contact_support_action_button_text,
                onSecondaryAction = { onErrorEvent ->
                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
                },
            )
    }
