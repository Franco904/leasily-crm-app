package com.fstengineering.leasilycrm.core.domain.utils

sealed interface DomainError : Error {
    enum class AssistanceInteractionDate : DomainError {
        IsNull,
    }

    enum class AssistanceInteractionTitle : DomainError {
        IsNullOrBlank,
    }

    enum class AssistanceSearchedNeighborhoodName : DomainError {
        IsNullOrBlank,
    }

    enum class CustomerSegmentName : DomainError {
        IsBlank,
        BelongsToOtherTenantSegment,
        BelongsToOtherPropertyOwnerSegment,
        LengthIsGreaterThan75Chars,
    }

    enum class CustomerSegmentType : DomainError {
        IsNull,
    }

    enum class PropertyPropertyOwner : DomainError {
        IsNull,
    }

    enum class PropertyType : DomainError {
        IsNull,
    }

    enum class PropertyNeighborhood : DomainError {
        IsBlank,
    }

    enum class PropertyStreetAddress : DomainError {
        IsBlank,
    }

    enum class PropertyDistanceToSea : DomainError {
        IsBlank,
    }

    enum class PropertyPricePeriod : DomainError {
        IsNull,
        StartAndEndDaysAreTheSame,
        IsOverlappingOtherPropertyPricePeriod,
    }

    enum class PropertyPriceAmount : DomainError {
        IsNull,
    }

    enum class PropertyOwnerName : DomainError {
        IsBlank,
        BelongsToOtherPropertyOwner,
        LengthIsGreaterThan100Chars,
    }

    enum class PropertyOwnerPhone : DomainError {
        LengthIsGreaterThan20Chars,
    }

    enum class PropertyOwnerEmail : DomainError {
        InvalidFormat,
        LengthIsGreaterThan120Chars,
    }

    enum class PropertyOwnerPaymentKey : DomainError {
        LengthIsGreaterThan120Chars,
    }

    enum class TenantName : DomainError {
        IsBlank,
        BelongsToOtherTenant,
        LengthIsGreaterThan100Chars,
    }

    enum class TenantPhone : DomainError {
        LengthIsGreaterThan20Chars,
    }

    enum class TenantEmail : DomainError {
        InvalidFormat,
        LengthIsGreaterThan120Chars,
    }

    enum class StayProperty : DomainError {
        IsNull,
    }

    enum class StayTenant : DomainError {
        IsNull,
    }

    enum class StayPeriod : DomainError {
        IsNull,
        StartAndEndDaysAreTheSame,
        IsOverlappingOtherPropertyStayPeriod,
    }

    enum class StayCheckInTime : DomainError {
        IsNull,
    }

    enum class StayCheckOutTime : DomainError {
        IsNull,
    }

    enum class StayCommission : DomainError {
        IsNull,
    }

    enum class StayTransferDate : DomainError {
        IsNull,
    }

    enum class StayTransferAmount : DomainError {
        IsNull,
    }

    enum class UserName : DomainError {
        IsBlank,
        LengthIsGreaterThan75Chars,
    }

    enum class CreciNumber : DomainError {
        LengthIsGreaterThan8Chars,
    }
}
