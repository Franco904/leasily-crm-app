package com.fstengineering.leasilycrm.signin.models.strategies

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy

data object SignInErrorBottomSheetDataPropertiesStrategy : ErrorBottomSheetDataPropertiesStrategy {
    override val noInternetErrorDescription: Int get() = R.string.no_internet_error_description_sign_in
    override val unknownErrorDescription: Int get() = R.string.unknown_error_description_sign_in
}
