package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError

private val INLINE_REQUIRED = R.string.inline_required
private val INLINE_GENERIC_INVALID_DATA = R.string.inline_invalid_data

fun ParsingError.toInlineMessage() = when (this) {
    ParsingError.Assistance.SearchingPeriodRequired -> INLINE_REQUIRED
    ParsingError.Assistance.SearchingPeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    ParsingError.Assistance.SearchingPeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    ParsingError.Assistance.AdultGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.KidsGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.BabiesGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.AnimalsGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.BedroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.EnSuitesCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.DoubleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.SingleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.BathroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.ParkingSpotsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Assistance.SearchedNeighborhoodNameRequired -> INLINE_REQUIRED
    ParsingError.Assistance.InteractionDateRequired -> INLINE_REQUIRED
    ParsingError.Assistance.InteractionTitleRequired -> INLINE_REQUIRED

    ParsingError.Property.OwnerRequired -> INLINE_REQUIRED
    ParsingError.Property.TypeRequired -> INLINE_REQUIRED
    ParsingError.Property.TypeInvalid -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Property.NeighborhoodRequired -> INLINE_REQUIRED
    ParsingError.Property.StreetAddressRequired -> INLINE_REQUIRED
    ParsingError.Property.CharacteristicInvalid -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Property.PricePeriodRequired -> INLINE_REQUIRED
    ParsingError.Property.PricePeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    ParsingError.Property.PricePeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    ParsingError.Property.PricePeriodIsOverlappingOtherPricePeriod -> R.string.inline_period_is_overlapping_other_property_price_period
    ParsingError.Property.PriceAmountRequired -> INLINE_REQUIRED
    ParsingError.Property.PriceAmountIsNegative -> INLINE_GENERIC_INVALID_DATA

    ParsingError.PropertyOwner.NameRequired -> INLINE_REQUIRED
    ParsingError.PropertyOwner.NameBelongsToOtherOwner -> R.string.inline_name_belongs_other_property_owner
    ParsingError.PropertyOwner.NameLengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars
    ParsingError.PropertyOwner.PhoneLengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars
    ParsingError.PropertyOwner.EmailInvalidFormat -> R.string.inline_invalid_email
    ParsingError.PropertyOwner.EmailLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars
    ParsingError.PropertyOwner.PaymentKeyLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    ParsingError.PropertySeaProximity.PropertySeaProximityInvalid -> INLINE_GENERIC_INVALID_DATA
    ParsingError.PropertySeaProximity.DistanceToSeaRequired -> INLINE_REQUIRED
    ParsingError.PropertySeaProximity.DistanceToSeaIsNegative -> INLINE_GENERIC_INVALID_DATA

    ParsingError.Stay.PropertyRequired -> INLINE_REQUIRED
    ParsingError.Stay.TenantRequired -> INLINE_REQUIRED
    ParsingError.Stay.GuestsCountRequired -> INLINE_REQUIRED
    ParsingError.Stay.GuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Stay.PeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    ParsingError.Stay.PeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    ParsingError.Stay.PeriodIsOverlappingOtherPeriod -> R.string.inline_period_is_overlapping_other_property_stay_period
    ParsingError.Stay.CheckInDateRequired -> INLINE_REQUIRED
    ParsingError.Stay.CheckInTimeRequired -> INLINE_REQUIRED
    ParsingError.Stay.CheckOutDateRequired -> INLINE_REQUIRED
    ParsingError.Stay.CheckOutTimeRequired -> INLINE_REQUIRED
    ParsingError.Stay.IsAutoCheckInRequired -> INLINE_REQUIRED
    ParsingError.Stay.IsAutoCheckOutRequired -> INLINE_REQUIRED
    ParsingError.Stay.TotalPriceIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Stay.CommissionRequired -> INLINE_REQUIRED
    ParsingError.Stay.CommissionIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Stay.TransferDateRequired -> INLINE_REQUIRED
    ParsingError.Stay.TransferAmountRequired -> INLINE_REQUIRED
    ParsingError.Stay.TransferAmountIsNegative -> INLINE_GENERIC_INVALID_DATA
    ParsingError.Stay.StatusRequired -> INLINE_REQUIRED
    ParsingError.Stay.StatusInvalid -> INLINE_GENERIC_INVALID_DATA

    ParsingError.Tenant.NameRequired -> INLINE_REQUIRED
    ParsingError.Tenant.NameBelongsToOtherTenant -> R.string.inline_name_belongs_other_tenant
    ParsingError.Tenant.NameLengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars
    ParsingError.Tenant.PhoneLengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars
    ParsingError.Tenant.EmailInvalidFormat -> R.string.inline_invalid_email
    ParsingError.Tenant.EmailLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars
    ParsingError.Tenant.StatusInvalid -> INLINE_GENERIC_INVALID_DATA

    ParsingError.User.CreciNumberLengthIsGreaterThan8Chars -> R.string.inline_too_big_8_chars
}
