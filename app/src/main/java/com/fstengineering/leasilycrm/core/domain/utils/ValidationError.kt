package com.fstengineering.leasilycrm.core.domain.utils

sealed interface ValidationError : Error {
    enum class CustomerSegmentName : ValidationError {
        IsBlank,
        LengthIsGreaterThan75Chars,
    }

    enum class CustomerSegmentType : ValidationError {
        IsNull,
    }

    enum class PropertyOwnerName : ValidationError {
        IsBlank,
        LengthIsGreaterThan100Chars,
    }

    enum class PropertyOwnerPhone : ValidationError {
        LengthIsGreaterThan20Chars,
    }

    enum class PropertyOwnerEmail : ValidationError {
        InvalidFormat,
        LengthIsGreaterThan120Chars,
    }

    enum class PropertyOwnerPaymentKey : ValidationError {
        LengthIsGreaterThan120Chars,
    }

    enum class PropertyPropertyOwner : ValidationError {
        IsNull,
    }

    enum class PropertyType : ValidationError {
        IsNull,
    }

    enum class PropertyNeighborhood : ValidationError {
        IsBlank,
    }

    enum class PropertyStreetAddress : ValidationError {
        IsBlank,
    }

    enum class PropertyDistanceToSea : ValidationError {
        IsBlank,
    }

    enum class PropertyPriceDateRange : ValidationError {
        IsNull,
    }

    enum class PropertyPriceAmount : ValidationError {
        IsNull,
    }

    enum class UserName : ValidationError {
        IsBlank,
        LengthIsGreaterThan75Chars,
    }

    enum class CreciNumber : ValidationError {
        LengthIsGreaterThan8Chars,
    }
}
