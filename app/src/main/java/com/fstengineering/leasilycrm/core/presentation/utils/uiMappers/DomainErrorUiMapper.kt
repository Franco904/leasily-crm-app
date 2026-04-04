package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

private val INLINE_REQUIRED = R.string.inline_required
private val INLINE_GENERIC_INVALID_DATA = R.string.inline_invalid_data

fun DomainError.toInlineMessage() = when (this) {
    DomainError.Amount.Required -> INLINE_REQUIRED
    DomainError.Amount.IsNegative -> INLINE_GENERIC_INVALID_DATA

    DomainError.AssistanceInteractionDate.Required -> INLINE_REQUIRED
    DomainError.AssistanceInteractionTitle.Required -> INLINE_REQUIRED

    DomainError.AssistanceOtherFields.TenantRequired -> INLINE_REQUIRED
    DomainError.AssistanceOtherFields.PreferredPeriodRequired -> INLINE_REQUIRED
    DomainError.AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.AssistanceOtherFields.AdultGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.KidsGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.BabiesGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.AnimalsGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.BedroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.EnSuitesCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.DoubleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.SingleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.BathroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.AssistanceOtherFields.ParkingSpotsCountIsNegative -> INLINE_GENERIC_INVALID_DATA

    DomainError.Creci.LengthIsGreaterThan8Chars -> R.string.inline_too_big_8_chars

    DomainError.Email.Invalid -> R.string.inline_invalid_email
    DomainError.Email.LengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    DomainError.Name.Required -> INLINE_REQUIRED
    DomainError.Name.LengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars

    DomainError.Neighborhood.Required -> INLINE_REQUIRED

    DomainError.PaymentKey.LengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    DomainError.Phone.LengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars

    DomainError.PropertyPropertyOwner.Required -> INLINE_REQUIRED
    DomainError.PropertyType.Required -> INLINE_REQUIRED
    DomainError.PropertyStreetAddress.Required -> INLINE_REQUIRED
    DomainError.PropertyPricePeriod.Required -> INLINE_REQUIRED
    DomainError.PropertyPricePeriod.StartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.PropertyPricePeriod.StartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.PropertyPricePeriod.IsOverlappingOtherPricePeriod -> R.string.inline_period_is_overlapping_other_property_price_period

    DomainError.PropertySeaProximity.PropertySeaProximityInvalid -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertySeaProximityDistanceToSea.Required -> INLINE_REQUIRED
    DomainError.PropertySeaProximityDistanceToSea.IsNegative -> INLINE_GENERIC_INVALID_DATA

    DomainError.PropertyOtherFields.BedroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.EnSuitesCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.DoubleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.SingleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.BathroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.ParkingSpotsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.AirConditionersCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.MaxGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertyOtherFields.MoreThanOneMainPictureFile -> INLINE_GENERIC_INVALID_DATA

    DomainError.StayProperty.Required -> INLINE_REQUIRED
    DomainError.StayTenant.Required -> INLINE_REQUIRED
    DomainError.StayCheckIn.Required -> INLINE_REQUIRED
    DomainError.StayCheckOut.Required -> INLINE_REQUIRED
    DomainError.StayPeriod.StartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.StayPeriod.StartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.StayPeriod.Overbooking -> R.string.inline_period_is_overlapping_other_property_stay_period
    DomainError.StayTotalPrice.Required -> INLINE_REQUIRED
    DomainError.StayTotalPrice.IsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.StayCommission.Required -> INLINE_REQUIRED
    DomainError.StayCommission.IsNegative -> INLINE_GENERIC_INVALID_DATA

    DomainError.StayOtherFields.GuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.StayOtherFields.StatusRequired -> INLINE_REQUIRED
}
