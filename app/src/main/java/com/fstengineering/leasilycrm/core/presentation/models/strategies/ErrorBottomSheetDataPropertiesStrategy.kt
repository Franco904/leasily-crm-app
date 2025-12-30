package com.fstengineering.leasilycrm.core.presentation.models.strategies

import com.fstengineering.leasilycrm.R

interface ErrorBottomSheetDataPropertiesStrategy {
    val noInternetErrorTitle: Int get() = R.string.no_internet_error_title
    val noInternetErrorDescription: Int get() = R.string.no_internet_error_description_default

    val timeOutErrorTitle: Int get() = R.string.time_out_error_title
    val timeOutErrorDescription: Int get() = R.string.time_out_error_description

    val unauthorizedErrorTitle: Int get() = R.string.unauthorized_error_title
    val unauthorizedErrorDescription: Int get() = R.string.unauthorized_error_description

    val unknownErrorTitle: Int get() = R.string.unknown_error_title
    val unknownErrorDescription: Int get() = R.string.unknown_error_description_default
}