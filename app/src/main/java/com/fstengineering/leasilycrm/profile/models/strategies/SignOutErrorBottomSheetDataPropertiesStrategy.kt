package com.fstengineering.leasilycrm.profile.models.strategies

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy

data object SignOutErrorBottomSheetDataPropertiesStrategy : ErrorBottomSheetDataPropertiesStrategy {
    override val unknownErrorDescription: Int get() = R.string.unknown_error_description_sign_out
}