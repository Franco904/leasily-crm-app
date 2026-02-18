package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

private val INLINE_REQUIRED = R.string.inline_required
private val INLINE_GENERIC_INVALID_DATA = R.string.inline_invalid_data

fun DomainError.toInlineMessage() = when (this) {
    DomainError.Assistance.SearchingPeriodRequired -> INLINE_REQUIRED
    DomainError.Assistance.SearchingPeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.Assistance.SearchingPeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.Assistance.AdultGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.KidsGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.BabiesGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.AnimalsGuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.BedroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.EnSuitesCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.DoubleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.SingleBedsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.BathroomsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.ParkingSpotsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Assistance.SearchedNeighborhoodNameRequired -> INLINE_REQUIRED
    DomainError.Assistance.InteractionDateRequired -> INLINE_REQUIRED
    DomainError.Assistance.InteractionTitleRequired -> INLINE_REQUIRED

    DomainError.Property.OwnerRequired -> INLINE_REQUIRED
    DomainError.Property.TypeRequired -> INLINE_REQUIRED
    DomainError.Property.TypeInvalid -> INLINE_GENERIC_INVALID_DATA
    DomainError.Property.NeighborhoodRequired -> INLINE_REQUIRED
    DomainError.Property.StreetAddressRequired -> INLINE_REQUIRED
    DomainError.Property.CharacteristicInvalid -> INLINE_GENERIC_INVALID_DATA
    DomainError.Property.PricePeriodRequired -> INLINE_REQUIRED
    DomainError.Property.PricePeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.Property.PricePeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.Property.PricePeriodIsOverlappingOtherPricePeriod -> R.string.inline_period_is_overlapping_other_property_price_period
    DomainError.Property.PriceAmountRequired -> INLINE_REQUIRED
    DomainError.Property.PriceAmountIsNegative -> INLINE_GENERIC_INVALID_DATA

    DomainError.PropertyOwner.NameRequired -> INLINE_REQUIRED
    DomainError.PropertyOwner.NameBelongsToOtherOwner -> R.string.inline_name_belongs_other_property_owner
    DomainError.PropertyOwner.NameLengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars
    DomainError.PropertyOwner.PhoneLengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars
    DomainError.PropertyOwner.EmailInvalidFormat -> R.string.inline_invalid_email
    DomainError.PropertyOwner.EmailLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars
    DomainError.PropertyOwner.PaymentKeyLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    DomainError.PropertySeaProximity.PropertySeaProximityInvalid -> INLINE_GENERIC_INVALID_DATA
    DomainError.PropertySeaProximity.DistanceToSeaRequired -> INLINE_REQUIRED
    DomainError.PropertySeaProximity.DistanceToSeaIsNegative -> INLINE_GENERIC_INVALID_DATA

    DomainError.Stay.PropertyRequired -> INLINE_REQUIRED
    DomainError.Stay.TenantRequired -> INLINE_REQUIRED
    DomainError.Stay.GuestsCountRequired -> INLINE_REQUIRED
    DomainError.Stay.GuestsCountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Stay.PeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    DomainError.Stay.PeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    DomainError.Stay.Overbooking -> R.string.inline_period_is_overlapping_other_property_stay_period
    DomainError.Stay.CheckInDateRequired -> INLINE_REQUIRED
    DomainError.Stay.CheckInTimeRequired -> INLINE_REQUIRED
    DomainError.Stay.CheckOutDateRequired -> INLINE_REQUIRED
    DomainError.Stay.CheckOutTimeRequired -> INLINE_REQUIRED
    DomainError.Stay.IsAutoCheckInRequired -> INLINE_REQUIRED
    DomainError.Stay.IsAutoCheckOutRequired -> INLINE_REQUIRED
    DomainError.Stay.TotalPriceIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Stay.CommissionRequired -> INLINE_REQUIRED
    DomainError.Stay.CommissionIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Stay.TransferDateRequired -> INLINE_REQUIRED
    DomainError.Stay.TransferAmountRequired -> INLINE_REQUIRED
    DomainError.Stay.TransferAmountIsNegative -> INLINE_GENERIC_INVALID_DATA
    DomainError.Stay.StatusRequired -> INLINE_REQUIRED
    DomainError.Stay.StatusInvalid -> INLINE_GENERIC_INVALID_DATA

    DomainError.Tenant.NameRequired -> INLINE_REQUIRED
    DomainError.Tenant.NameBelongsToOtherTenant -> R.string.inline_name_belongs_other_tenant
    DomainError.Tenant.NameLengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars
    DomainError.Tenant.PhoneLengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars
    DomainError.Tenant.EmailInvalidFormat -> R.string.inline_invalid_email
    DomainError.Tenant.EmailLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars
    DomainError.Tenant.StatusInvalid -> INLINE_GENERIC_INVALID_DATA

    DomainError.User.CreciNumberLengthIsGreaterThan8Chars -> R.string.inline_too_big_8_chars
}
