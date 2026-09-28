package com.fstengineering.leasilycrm.core.domain.utils

sealed interface DomainError : BaseError {
    enum class Amount : DomainError {
        Required,
    }

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
    }

    enum class Neighborhood : DomainError {
        Required,
    }

    enum class PropertyPropertyOwner : DomainError {
        Required,
    }

    enum class PropertyType : DomainError {
        Required,
    }

    enum class PropertyStreetAddress : DomainError {
        Required,
    }

    enum class PropertyPricePeriod : DomainError {
        Required,
        StartAndEndDatesAreTheSame,
        StartDateIsAfterEndDate,
        IsOverlappingOtherPricePeriod,
    }

    enum class PropertyOtherFields : DomainError {
        MoreThanOneMainPictureFile,
    }

    enum class PropertySeaProximity : DomainError {
        PropertySeaProximityInvalid,
    }

    enum class PropertySeaProximityDistanceToSea : DomainError {
        Required,
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

    enum class StayProperty : DomainError {
        Required,
    }

    enum class StayTenant : DomainError {
        Required,
    }

    enum class StayPeriod : DomainError {
        Required,
        StartAndEndDatesAreTheSame,
        StartDateIsAfterEndDate,
        Overbooking,
    }

    enum class StayCheckInTime : DomainError {
        Required,
    }

    enum class StayCheckOutTime : DomainError {
        Required,
    }

    enum class StayCommission : DomainError {
        Required,
    }

    enum class Creci : DomainError {
        LengthIsGreaterThan8Chars,
    }
}
