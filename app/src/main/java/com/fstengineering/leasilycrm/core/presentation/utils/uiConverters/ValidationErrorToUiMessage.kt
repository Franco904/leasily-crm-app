package com.fstengineering.leasilycrm.core.presentation.utils.uiConverters

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

fun ValidationError.toUiMessage() = when (this) {
    ValidationError.UserName.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.UserName.LengthIsGreaterThan75Chars -> {
        R.string.inline_user_name_too_big
    }

    ValidationError.CreciNumber.LengthIsGreaterThan8Chars -> {
        R.string.inline_user_name_too_big
    }
}
