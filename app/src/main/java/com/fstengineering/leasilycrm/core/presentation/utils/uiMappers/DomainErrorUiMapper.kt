package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

private val INLINE_REQUIRED = R.string.inline_required
private val INLINE_GENERIC_INVALID_DATA = R.string.inline_invalid_data

fun DomainError.toInlineMessage() = when (this) {
    DomainError.Amount.Required -> INLINE_REQUIRED

    DomainError.AssistanceInteractionDate.Required -> INLINE_REQUIRED
    DomainError.AssistanceInteractionTitle.Required -> INLINE_REQUIRED

    DomainError.AssistanceOtherFields.TenantRequired -> INLINE_REQUIRED
    DomainError.AssistanceOtherFields.PreferredPeriodRequired -> INLINE_REQUIRED
    DomainError.AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period

    DomainError.Creci.LengthIsGreaterThan8Chars -> R.string.inline_too_big_8_chars

    DomainError.Email.Invalid -> R.string.inline_invalid_email
    DomainError.Email.LengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    DomainError.Name.Required -> INLINE_REQUIRED
    DomainError.Name.LengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars

    DomainError.Neighborhood.Required -> INLINE_REQUIRED

    DomainError.PaymentKey.LengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    DomainError.Phone.LengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars

    DomainError.ImovelProprietario.Required -> INLINE_REQUIRED
    DomainError.ImovelType.Required -> INLINE_REQUIRED
    DomainError.ImovelStreetAddress.Required -> INLINE_REQUIRED
    DomainError.ImovelPricePeriod.Required -> INLINE_REQUIRED
    DomainError.ImovelPricePeriod.StartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.ImovelPricePeriod.StartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.ImovelPricePeriod.IsOverlappingOtherPricePeriod -> R.string.inline_period_is_overlapping_other_property_price_period

    DomainError.ImovelSeaProximity.ImovelSeaProximityInvalid -> INLINE_GENERIC_INVALID_DATA
    DomainError.ImovelSeaProximityDistanceToSea.Required -> INLINE_REQUIRED

    DomainError.ImovelOtherFields.MoreThanOneMainPictureFile -> INLINE_GENERIC_INVALID_DATA

    DomainError.StayProperty.Required -> INLINE_REQUIRED
    DomainError.StayTenant.Required -> INLINE_REQUIRED
    DomainError.StayCheckInTime.Required -> INLINE_REQUIRED
    DomainError.StayCheckOutTime.Required -> INLINE_REQUIRED
    DomainError.StayPeriod.Required -> INLINE_REQUIRED
    DomainError.StayPeriod.StartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.StayPeriod.StartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.StayPeriod.Overbooking -> R.string.inline_period_is_overlapping_other_property_stay_period
    DomainError.StayCommission.Required -> INLINE_REQUIRED
}
