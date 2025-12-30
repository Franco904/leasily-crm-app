package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

fun DomainError.toInlineMessage() = when (this) {
    DomainError.AssistanceInteractionDate.IsNull -> {
        R.string.inline_required
    }

    DomainError.AssistanceInteractionTitle.IsNullOrBlank -> {
        R.string.inline_required
    }

    DomainError.AssistanceSearchedNeighborhoodName.IsNullOrBlank -> {
        R.string.inline_required
    }

    DomainError.CustomerSegmentName.IsBlank -> {
        R.string.inline_required
    }

    DomainError.CustomerSegmentName.BelongsToOtherTenantSegment -> {
        R.string.inline_name_belongs_other_tenant_segment
    }

    DomainError.CustomerSegmentName.BelongsToOtherPropertyOwnerSegment -> {
        R.string.inline_name_belongs_other_property_owner_segment
    }

    DomainError.CustomerSegmentName.LengthIsGreaterThan75Chars -> {
        R.string.inline_too_big_75_chars
    }

    DomainError.CustomerSegmentType.IsNull -> {
        R.string.inline_required
    }

    DomainError.PropertyPropertyOwner.IsNull -> {
        R.string.inline_required
    }

    DomainError.PropertyType.IsNull -> {
        R.string.inline_required
    }

    DomainError.PropertyNeighborhood.IsBlank -> {
        R.string.inline_required
    }

    DomainError.PropertyStreetAddress.IsBlank -> {
        R.string.inline_required
    }

    DomainError.PropertyDistanceToSea.IsBlank -> {
        R.string.inline_required
    }

    DomainError.PropertyPricePeriod.IsNull -> {
        R.string.inline_required
    }

    DomainError.PropertyPricePeriod.StartAndEndDaysAreTheSame -> {
        R.string.inline_period_start_end_date_are_same_period
    }

    DomainError.PropertyPricePeriod.IsOverlappingOtherPropertyPricePeriod -> {
        R.string.inline_period_is_overlapping_other_property_price_period
    }

    DomainError.PropertyPriceAmount.IsNull -> {
        R.string.inline_required
    }

    DomainError.PropertyOwnerName.IsBlank -> {
        R.string.inline_required
    }

    DomainError.PropertyOwnerName.BelongsToOtherPropertyOwner -> {
        R.string.inline_name_belongs_other_property_owner
    }

    DomainError.PropertyOwnerName.LengthIsGreaterThan100Chars -> {
        R.string.inline_too_big_100_chars
    }

    DomainError.PropertyOwnerPhone.LengthIsGreaterThan20Chars -> {
        R.string.inline_too_big_20_chars
    }

    DomainError.PropertyOwnerEmail.InvalidFormat -> {
        R.string.inline_invalid_email
    }

    DomainError.PropertyOwnerEmail.LengthIsGreaterThan120Chars -> {
        R.string.inline_too_big_120_chars
    }

    DomainError.PropertyOwnerPaymentKey.LengthIsGreaterThan120Chars -> {
        R.string.inline_too_big_120_chars
    }

    DomainError.TenantName.IsBlank -> {
        R.string.inline_required
    }

    DomainError.TenantName.BelongsToOtherTenant -> {
        R.string.inline_name_belongs_other_tenant
    }

    DomainError.TenantName.LengthIsGreaterThan100Chars -> {
        R.string.inline_too_big_100_chars
    }

    DomainError.TenantPhone.LengthIsGreaterThan20Chars -> {
        R.string.inline_too_big_20_chars
    }

    DomainError.TenantEmail.InvalidFormat -> {
        R.string.inline_invalid_email
    }

    DomainError.TenantEmail.LengthIsGreaterThan120Chars -> {
        R.string.inline_too_big_120_chars
    }

    DomainError.StayProperty.IsNull -> {
        R.string.inline_required
    }

    DomainError.StayTenant.IsNull -> {
        R.string.inline_required
    }

    DomainError.StayPeriod.IsNull -> {
        R.string.inline_required
    }

    DomainError.StayPeriod.StartAndEndDaysAreTheSame -> {
        R.string.inline_period_start_end_date_are_same_period
    }

    DomainError.StayPeriod.IsOverlappingOtherPropertyStayPeriod -> {
        R.string.inline_period_is_overlapping_other_property_stay_period
    }

    DomainError.StayCheckInTime.IsNull -> {
        R.string.inline_required
    }

    DomainError.StayCheckOutTime.IsNull -> {
        R.string.inline_required
    }

    DomainError.StayCommission.IsNull -> {
        R.string.inline_required
    }

    DomainError.StayTransferDate.IsNull -> {
        R.string.inline_required
    }

    DomainError.StayTransferAmount.IsNull -> {
        R.string.inline_required
    }

    DomainError.UserName.IsBlank -> {
        R.string.inline_required
    }

    DomainError.UserName.LengthIsGreaterThan75Chars -> {
        R.string.inline_too_big_75_chars
    }

    DomainError.CreciNumber.LengthIsGreaterThan8Chars -> {
        R.string.inline_too_big_75_chars
    }
}
