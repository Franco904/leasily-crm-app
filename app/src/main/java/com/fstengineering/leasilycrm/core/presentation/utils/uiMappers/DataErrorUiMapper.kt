package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy

fun DataError.toBottomSheetData(properties: ErrorBottomSheetDataPropertiesStrategy): ErrorBottomSheetData =
    when (this) {
        DataError.NoInternetError ->
            ErrorBottomSheetData(
                title = properties.noInternetErrorTitle,
                description = properties.noInternetErrorDescription
            )

        DataError.TimeOutError ->
            ErrorBottomSheetData(
                title = properties.timeOutErrorTitle,
                description = properties.timeOutErrorDescription
            )

        DataError.UnauthorizedError ->
            ErrorBottomSheetData(
                title = properties.unauthorizedErrorTitle,
                description = properties.unauthorizedErrorDescription
            )

        DataError.UnknownError ->
            ErrorBottomSheetData(
                title = properties.unknownErrorTitle,
                description = properties.unknownErrorDescription
            )
    }
