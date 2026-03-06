package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.strategies.ErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.mailToSupportChannel

fun ApplicationError.toBottomSheetData(
    properties: ErrorBottomSheetDataPropertiesStrategy,
): ErrorBottomSheetData =
    when (this) {
        ApplicationError.Infra.NoInternet -> {
            ErrorBottomSheetData(
                title = properties.noInternetErrorTitle,
                description = properties.noInternetErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Infra.Timeout -> {
            ErrorBottomSheetData(
                title = properties.timeOutErrorTitle,
                description = properties.timeOutErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

        ApplicationError.Infra.Unauthorized -> {
            ErrorBottomSheetData(
                title = properties.unauthorizedErrorTitle,
                description = properties.unauthorizedErrorDescription,
                onSecondaryAction = null,
                secondaryActionLabel = null,
            )
        }

//        ApplicationError.Infra.Unknown -> {
//            ErrorBottomSheetData(
//                title = properties.inconsistencyErrorTitle,
//                description = properties.inconsistencyErrorDescription,
//                secondaryActionLabel = R.string.contact_support_action_button_text,
//                onSecondaryAction = { onErrorEvent ->
//                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
//                },
//            )
//        }

        else -> {
            ErrorBottomSheetData(
                title = properties.unknownErrorTitle,
                description = properties.unknownErrorDescription,
                secondaryActionLabel = R.string.contact_support_action_button_text,
                onSecondaryAction = { onErrorEvent ->
                    mailToSupportChannel { errorUiEvent -> onErrorEvent(errorUiEvent) }
                },
            )
        }
    }

private val INLINE_REQUIRED = R.string.inline_required
private val INLINE_GENERIC_INVALID_DATA = R.string.inline_invalid_data

fun ApplicationError.toInlineMessage() = when (this) {
    ApplicationError.AssistanceInteractionDate.Required -> INLINE_REQUIRED
    ApplicationError.AssistanceInteractionTitle.Required -> INLINE_REQUIRED

    ApplicationError.AssistanceOtherFields.TenantRequired -> INLINE_REQUIRED
    ApplicationError.AssistanceOtherFields.PreferredPeriodRequired -> INLINE_REQUIRED
    ApplicationError.AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    ApplicationError.AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period

    ApplicationError.Neighborhood.Required -> INLINE_REQUIRED

    ApplicationError.Property.OwnerRequired -> INLINE_REQUIRED
    ApplicationError.Property.TypeRequired -> INLINE_REQUIRED
    ApplicationError.Property.StreetAddressRequired -> INLINE_REQUIRED
    ApplicationError.Property.PricePeriodRequired -> INLINE_REQUIRED
    ApplicationError.Property.PricePeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    ApplicationError.Property.PricePeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    ApplicationError.Property.PricePeriodIsOverlappingOtherPricePeriod -> R.string.inline_period_is_overlapping_other_property_price_period
    ApplicationError.Property.PriceAmountRequired -> INLINE_REQUIRED

    ApplicationError.PropertyOwner.NameRequired -> INLINE_REQUIRED
    ApplicationError.PropertyOwner.NameBelongsToOtherOwner -> R.string.inline_name_belongs_other_property_owner
    ApplicationError.PropertyOwner.NameLengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars
    ApplicationError.PropertyOwner.PhoneLengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars
    ApplicationError.PropertyOwner.EmailInvalidFormat -> R.string.inline_invalid_email
    ApplicationError.PropertyOwner.EmailLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars
    ApplicationError.PropertyOwner.PaymentKeyLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    ApplicationError.PropertySeaProximityDistanceToSea.Required -> INLINE_REQUIRED

    ApplicationError.Stay.PropertyRequired -> INLINE_REQUIRED
    ApplicationError.Stay.TenantRequired -> INLINE_REQUIRED
    ApplicationError.Stay.GuestsCountRequired -> INLINE_REQUIRED
    ApplicationError.Stay.PeriodStartAndEndDatesAreTheSame -> R.string.inline_period_start_end_date_are_same_period
    ApplicationError.Stay.PeriodStartDateIsAfterEndDate -> R.string.inline_period_start_date_is_after_end_date_period
    ApplicationError.Stay.Overbooking -> R.string.inline_period_is_overlapping_other_property_stay_period
    ApplicationError.Stay.CheckInDateRequired -> INLINE_REQUIRED
    ApplicationError.Stay.CheckInTimeRequired -> INLINE_REQUIRED
    ApplicationError.Stay.CheckOutDateRequired -> INLINE_REQUIRED
    ApplicationError.Stay.CheckOutTimeRequired -> INLINE_REQUIRED
    ApplicationError.Stay.IsAutoCheckInRequired -> INLINE_REQUIRED
    ApplicationError.Stay.IsAutoCheckOutRequired -> INLINE_REQUIRED
    ApplicationError.Stay.CommissionRequired -> INLINE_REQUIRED
    ApplicationError.Stay.TransferDateRequired -> INLINE_REQUIRED
    ApplicationError.Stay.TransferAmountRequired -> INLINE_REQUIRED
    ApplicationError.Stay.StatusRequired -> INLINE_REQUIRED

    ApplicationError.Tenant.NameRequired -> INLINE_REQUIRED
    ApplicationError.Tenant.NameBelongsToOtherTenant -> R.string.inline_name_belongs_other_tenant
    ApplicationError.Tenant.NameLengthIsGreaterThan100Chars -> R.string.inline_too_big_100_chars
    ApplicationError.Tenant.PhoneLengthIsGreaterThan20Chars -> R.string.inline_too_big_20_chars
    ApplicationError.Tenant.EmailInvalidFormat -> R.string.inline_invalid_email
    ApplicationError.Tenant.EmailLengthIsGreaterThan120Chars -> R.string.inline_too_big_120_chars

    ApplicationError.UserCreci.LengthIsGreaterThan8Chars -> R.string.inline_too_big_8_chars

    else -> INLINE_GENERIC_INVALID_DATA
}
