package com.fstengineering.leasilycrm.core.domain.utils

sealed interface DomainError : SystemError {
    fun asException() = DomainException(this)

    enum class Assistance : DomainError {
        SearchingPeriodRequired,
        SearchingPeriodStartAndEndDatesAreTheSame,
        SearchingPeriodStartDateIsAfterEndDate,
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
        SearchedNeighborhoodNameRequired,
        InteractionDateRequired,
        InteractionTitleRequired,
    }

    enum class Property : DomainError {
        OwnerRequired,
        TypeRequired,
        TypeInvalid,
        NeighborhoodRequired,
        StreetAddressRequired,
        CharacteristicInvalid,
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
        DistanceToSeaRequired,
        DistanceToSeaIsNegative,
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

    enum class User : DomainError {
        CreciNumberLengthIsGreaterThan8Chars,
    }
}
