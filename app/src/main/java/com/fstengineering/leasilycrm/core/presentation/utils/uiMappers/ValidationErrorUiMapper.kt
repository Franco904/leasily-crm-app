package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

fun ValidationError.toUiMessage() = when (this) {
    ValidationError.CustomerSegmentName.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.CustomerSegmentName.LengthIsGreaterThan75Chars -> {
        R.string.inline_too_big_75_chars
    }

    ValidationError.CustomerSegmentType.IsNull -> {
        R.string.inline_required
    }

    ValidationError.PropertyPropertyOwner.IsNull -> {
        R.string.inline_required
    }

    ValidationError.PropertyType.IsNull -> {
        R.string.inline_required
    }

    ValidationError.PropertyNeighborhood.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.PropertyStreetAddress.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.PropertyDistanceToSea.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.PropertyPriceDateRange.IsNull -> {
        R.string.inline_required
    }

    ValidationError.PropertyPriceAmount.IsNull -> {
        R.string.inline_required
    }

    ValidationError.PropertyOwnerName.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.PropertyOwnerName.LengthIsGreaterThan100Chars -> {
        R.string.inline_too_big_100_chars
    }

    ValidationError.PropertyOwnerPhone.LengthIsGreaterThan20Chars -> {
        R.string.inline_too_big_20_chars
    }

    ValidationError.PropertyOwnerEmail.InvalidFormat -> {
        R.string.inline_invalid_email
    }

    ValidationError.PropertyOwnerEmail.LengthIsGreaterThan120Chars -> {
        R.string.inline_too_big_120_chars
    }

    ValidationError.PropertyOwnerPaymentKey.LengthIsGreaterThan120Chars -> {
        R.string.inline_too_big_120_chars
    }

    ValidationError.TenantName.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.TenantName.LengthIsGreaterThan100Chars -> {
        R.string.inline_too_big_100_chars
    }

    ValidationError.TenantPhone.LengthIsGreaterThan20Chars -> {
        R.string.inline_too_big_20_chars
    }

    ValidationError.TenantEmail.InvalidFormat -> {
        R.string.inline_invalid_email
    }

    ValidationError.TenantEmail.LengthIsGreaterThan120Chars -> {
        R.string.inline_too_big_120_chars
    }

    ValidationError.UserName.IsBlank -> {
        R.string.inline_required
    }

    ValidationError.UserName.LengthIsGreaterThan75Chars -> {
        R.string.inline_too_big_75_chars
    }

    ValidationError.CreciNumber.LengthIsGreaterThan8Chars -> {
        R.string.inline_too_big_75_chars
    }
}
