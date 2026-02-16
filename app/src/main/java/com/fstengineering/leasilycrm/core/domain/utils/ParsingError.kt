package com.fstengineering.leasilycrm.core.domain.utils

sealed interface ParsingError : AppError {
    fun asException() = ParsingException(this)

    enum class Assistance : ParsingError {
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

    enum class Property : ParsingError {
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
    }

    enum class PropertySeaProximity : ParsingError {
        PropertySeaProximityInvalid,
        DistanceToSeaRequired,
        DistanceToSeaIsNegative,
    }

    enum class PropertyOwner : ParsingError {
        NameRequired,
        NameBelongsToOtherOwner,
        NameLengthIsGreaterThan100Chars,
        PhoneLengthIsGreaterThan20Chars,
        EmailInvalidFormat,
        EmailLengthIsGreaterThan120Chars,
        PaymentKeyLengthIsGreaterThan120Chars,
    }

    enum class Tenant : ParsingError {
        NameRequired,
        NameBelongsToOtherTenant,
        NameLengthIsGreaterThan100Chars,
        PhoneLengthIsGreaterThan20Chars,
        EmailInvalidFormat,
        EmailLengthIsGreaterThan120Chars,
        StatusInvalid,
    }

    enum class Stay : ParsingError {
        PropertyRequired,
        TenantRequired,
        GuestsCountRequired,
        GuestsCountIsNegative,
        PeriodStartAndEndDatesAreTheSame,
        PeriodStartDateIsAfterEndDate,
        PeriodIsOverlappingOtherPeriod,
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

    enum class User : ParsingError {
        CreciNumberLengthIsGreaterThan8Chars,
    }
}
