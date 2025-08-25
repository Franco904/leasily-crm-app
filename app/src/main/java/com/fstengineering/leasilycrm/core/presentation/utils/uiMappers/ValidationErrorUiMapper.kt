package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

fun ValidationError.toUiMessage() = when (this) {
    ValidationError.CustomerSegmentName.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.CustomerSegmentName.LengthIsGreaterThan75Chars -> {
        R.string.inline_user_name_too_big_75_chars
    }

    ValidationError.CustomerSegmentType.IsNull -> {
        R.string.inline_required
    }

    ValidationError.UserName.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.UserName.LengthIsGreaterThan75Chars -> {
        R.string.inline_user_name_too_big_75_chars
    }

    ValidationError.CreciNumber.LengthIsGreaterThan8Chars -> {
        R.string.inline_user_name_too_big_75_chars
    }
}
