package com.fstengineering.leasilycrm.core.domain.utils

sealed interface DomainError : BusinessRuleError {
    fun asException() = DomainException(this)

    enum class AssistanceInteractionDate : DomainError {
        Required,
    }

    enum class AssistanceInteractionTitle : DomainError {
        Required,
    }

    enum class AssistanceOtherFields : DomainError {
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

    enum class Neighborhood : DomainError {
        Required,
    }

    enum class Property : DomainError {
        OwnerRequired,
        TypeRequired,
        StreetAddressRequired,
        PricePeriodRequired,
        PricePeriodStartAndEndDatesAreTheSame,
        PricePeriodStartDateIsAfterEndDate,
        PricePeriodIsOverlappingOtherPricePeriod,
        PriceAmountRequired,
        PriceAmountIsNegative,
        MoreThanOneMainPictureFile,
    }

    enum class PropertySeaProximity : DomainError {
        PropertySeaProximityInvalid,
    }

    enum class PropertySeaProximityDistanceToSea : DomainError {
        Required,
        IsNegative,
    }

    enum class Name : DomainError {
        Required,
        LengthIsGreaterThan100Chars,
    }

    enum class Phone : DomainError {
        LengthIsGreaterThan20Chars,
    }

    enum class Email : DomainError {
        Invalid,
        LengthIsGreaterThan120Chars,
    }

    enum class PaymentKey : DomainError {
        LengthIsGreaterThan120Chars,
    }

    enum class PropertyOwner : DomainError {
        NameRequired,
        NameBelongsToOtherOwner,
        NameLengthIsGreaterThan100Chars,
        PhoneLengthIsGreaterThan20Chars,
        EmailInvalidFormat,
        EmailLengthIsGreaterThan120Chars,
        PaymentKeyLengthIsGreaterThan120Chars,
    }

    enum class Tenant : DomainError {
        NameRequired,
        NameBelongsToOtherTenant,
        NameLengthIsGreaterThan100Chars,
        PhoneLengthIsGreaterThan20Chars,
        EmailInvalidFormat,
        EmailLengthIsGreaterThan120Chars,
        StatusInvalid,
    }

    enum class Stay : DomainError {
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

    enum class Creci : DomainError {
        LengthIsGreaterThan8Chars,
    }
}
