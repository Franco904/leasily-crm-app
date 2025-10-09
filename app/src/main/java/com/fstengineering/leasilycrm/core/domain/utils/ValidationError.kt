package com.fstengineering.leasilycrm.core.domain.utils

sealed interface ValidationError : Error {
    enum class AssistanceInteractionDate : ValidationError {
        IsNull,
    }

    enum class AssistanceInteractionTitle : ValidationError {
        IsNullOrBlank,
    }

    enum class AssistanceSearchedNeighborhoodName : ValidationError {
        IsNullOrBlank,
    }

    enum class CustomerSegmentName : ValidationError {
        IsBlank,
        BelongsToOtherTenantSegment,
        BelongsToOtherPropertyOwnerSegment,
        LengthIsGreaterThan75Chars,
    }

    enum class CustomerSegmentType : ValidationError {
        IsNull,
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

    enum class PropertyPricePeriod : ValidationError {
        IsNull,
        StartAndEndDaysAreTheSame,
        IsOverlappingOtherPropertyPricePeriod,
    }

    enum class PropertyPriceAmount : ValidationError {
        IsNull,
    }

    enum class PropertyOwnerName : ValidationError {
        IsBlank,
        BelongsToOtherPropertyOwner,
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

    enum class TenantName : ValidationError {
        IsBlank,
        BelongsToOtherTenant,
        LengthIsGreaterThan100Chars,
    }

    enum class TenantPhone : ValidationError {
        LengthIsGreaterThan20Chars,
    }

    enum class TenantEmail : ValidationError {
        InvalidFormat,
        LengthIsGreaterThan120Chars,
    }

    enum class StayProperty : ValidationError {
        IsNull,
    }

    enum class StayTenant : ValidationError {
        IsNull,
    }

    enum class StayPeriod : ValidationError {
        IsNull,
        StartAndEndDaysAreTheSame,
        IsOverlappingOtherPropertyStayPeriod,
    }

    enum class StayCheckInTime : ValidationError {
        IsNull,
    }

    enum class StayCheckOutTime : ValidationError {
        IsNull,
    }

    enum class StayTotalPrice : ValidationError {
        IsNull,
    }

    enum class StayTransferDate : ValidationError {
        IsNull,
    }

    enum class StayTransferAmount : ValidationError {
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
