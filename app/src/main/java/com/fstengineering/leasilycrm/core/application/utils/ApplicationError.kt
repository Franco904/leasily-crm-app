package com.fstengineering.leasilycrm.core.application.utils

import com.fstengineering.leasilycrm.core.domain.utils.BaseError

sealed interface ApplicationError : BaseError {
    enum class Infra : ApplicationError {
        NoInternet,
        Timeout,
        Unauthorized,
        Unknown,
    }

    enum class AssistanceInteractionDate : ApplicationError {
        Required,
    }

    enum class AssistanceInteractionTitle : ApplicationError {
        Required,
    }

    enum class AssistanceOtherFields : ApplicationError {
        TenantRequired,
        PreferredPeriodRequired,
        PreferredPeriodStartAndEndDatesAreTheSame,
        PreferredPeriodStartDateIsAfterEndDate,
        AdultGuestsCountIsNegative,
        KidsGuestsCountIsNegative,
        BabiesGuestsCountIsNegative,
        AnimalsGuestsCountIsNegative,
        BedroomsCountIsNegative,
        EnSuitesCountIsNegative,
        DoubleBedsCountIsNegative,
        SingleBedsCountIsNegative,
        BathroomsCountIsNegative,
        ParkingSpotsCountIsNegative,
    }

    enum class Neighborhood : ApplicationError {
        Required,
    }

    enum class Property : ApplicationError {
        OwnerRequired,
        TypeRequired,
        TypeInvalid,
        StreetAddressRequired,
        PricePeriodRequired,
        PricePeriodStartAndEndDatesAreTheSame,
        PricePeriodStartDateIsAfterEndDate,
        PricePeriodIsOverlappingOtherPricePeriod,
        PriceAmountRequired,
        PriceAmountIsNegative,
        MoreThanOneMainPictureFile,
    }

    enum class PropertySeaProximity : ApplicationError {
        PropertySeaProximityInvalid,
    }

    enum class PropertySeaProximityDistanceToSea : ApplicationError {
        Required,
        IsNegative,
    }

    enum class PropertyOwner : ApplicationError {
        NameRequired,
        NameBelongsToOtherOwner,
        NameLengthIsGreaterThan100Chars,
        PhoneLengthIsGreaterThan20Chars,
        EmailInvalidFormat,
        EmailLengthIsGreaterThan120Chars,
        PaymentKeyLengthIsGreaterThan120Chars,
    }

    enum class Tenant : ApplicationError {
        NameRequired,
        NameBelongsToOtherTenant,
        NameLengthIsGreaterThan100Chars,
        PhoneLengthIsGreaterThan20Chars,
        EmailInvalidFormat,
        EmailLengthIsGreaterThan120Chars,
        StatusInvalid,
    }

    enum class Stay : ApplicationError {
        PropertyRequired,
        TenantRequired,
        GuestsCountRequired,
        GuestsCountIsNegative,
        PeriodStartAndEndDatesAreTheSame,
        PeriodStartDateIsAfterEndDate,
        Overbooking,
        CheckInDateRequired,
        CheckInTimeRequired,
        CheckOutDateRequired,
        CheckOutTimeRequired,
        IsAutoCheckInRequired,
        IsAutoCheckOutRequired,
        TotalPriceIsNegative,
        CommissionRequired,
        CommissionIsNegative,
        TransferDateRequired,
        TransferAmountRequired,
        TransferAmountIsNegative,
        StatusRequired,
        StatusInvalid,
    }

    enum class UserCreci : ApplicationError {
        LengthIsGreaterThan8Chars,
    }
}
