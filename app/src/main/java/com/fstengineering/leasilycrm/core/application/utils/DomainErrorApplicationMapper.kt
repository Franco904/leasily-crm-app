package com.fstengineering.leasilycrm.core.application.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.AssistanceInteractionDate
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.AssistanceInteractionTitle
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.AssistanceOtherFields
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.Neighborhood
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.Property
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.PropertyOwner
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.PropertySeaProximity
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.PropertySeaProximityDistanceToSea
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.Stay
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.Tenant
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError.UserCreci
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

fun DomainError.toApplicationError(): ApplicationError = when (this) {
    DomainError.AssistanceInteractionDate.Required -> AssistanceInteractionDate.Required
    DomainError.AssistanceInteractionTitle.Required -> AssistanceInteractionTitle.Required

    DomainError.AssistanceOtherFields.TenantRequired -> AssistanceOtherFields.TenantRequired
    DomainError.AssistanceOtherFields.PreferredPeriodRequired -> AssistanceOtherFields.PreferredPeriodRequired
    DomainError.AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame -> AssistanceOtherFields.PreferredPeriodStartAndEndDatesAreTheSame
    DomainError.AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate -> AssistanceOtherFields.PreferredPeriodStartDateIsAfterEndDate
    DomainError.AssistanceOtherFields.AdultGuestsCountIsNegative -> AssistanceOtherFields.AdultGuestsCountIsNegative
    DomainError.AssistanceOtherFields.KidsGuestsCountIsNegative -> AssistanceOtherFields.KidsGuestsCountIsNegative
    DomainError.AssistanceOtherFields.BabiesGuestsCountIsNegative -> AssistanceOtherFields.BabiesGuestsCountIsNegative
    DomainError.AssistanceOtherFields.AnimalsGuestsCountIsNegative -> AssistanceOtherFields.AnimalsGuestsCountIsNegative
    DomainError.AssistanceOtherFields.BedroomsCountIsNegative -> AssistanceOtherFields.BedroomsCountIsNegative
    DomainError.AssistanceOtherFields.EnSuitesCountIsNegative -> AssistanceOtherFields.EnSuitesCountIsNegative
    DomainError.AssistanceOtherFields.DoubleBedsCountIsNegative -> AssistanceOtherFields.DoubleBedsCountIsNegative
    DomainError.AssistanceOtherFields.SingleBedsCountIsNegative -> AssistanceOtherFields.SingleBedsCountIsNegative
    DomainError.AssistanceOtherFields.BathroomsCountIsNegative -> AssistanceOtherFields.BathroomsCountIsNegative
    DomainError.AssistanceOtherFields.ParkingSpotsCountIsNegative -> AssistanceOtherFields.ParkingSpotsCountIsNegative

    DomainError.Neighborhood.Required -> Neighborhood.Required

    DomainError.Property.OwnerRequired -> Property.OwnerRequired
    DomainError.Property.TypeRequired -> Property.TypeRequired
    DomainError.Property.TypeInvalid -> Property.TypeInvalid
    DomainError.Property.StreetAddressRequired -> Property.StreetAddressRequired
    DomainError.Property.PricePeriodRequired -> Property.PricePeriodRequired
    DomainError.Property.PricePeriodStartAndEndDatesAreTheSame -> Property.PricePeriodStartAndEndDatesAreTheSame
    DomainError.Property.PricePeriodStartDateIsAfterEndDate -> Property.PricePeriodStartDateIsAfterEndDate
    DomainError.Property.PricePeriodIsOverlappingOtherPricePeriod -> Property.PricePeriodIsOverlappingOtherPricePeriod
    DomainError.Property.PriceAmountRequired -> Property.PriceAmountRequired
    DomainError.Property.PriceAmountIsNegative -> Property.PriceAmountIsNegative
    DomainError.Property.MoreThanOneMainPictureFile -> Property.MoreThanOneMainPictureFile

    DomainError.PropertyOwner.NameRequired -> PropertyOwner.NameRequired
    DomainError.PropertyOwner.NameBelongsToOtherOwner -> PropertyOwner.NameBelongsToOtherOwner
    DomainError.PropertyOwner.NameLengthIsGreaterThan100Chars -> PropertyOwner.NameLengthIsGreaterThan100Chars
    DomainError.PropertyOwner.PhoneLengthIsGreaterThan20Chars -> PropertyOwner.PhoneLengthIsGreaterThan20Chars
    DomainError.PropertyOwner.EmailInvalidFormat -> PropertyOwner.EmailInvalidFormat
    DomainError.PropertyOwner.EmailLengthIsGreaterThan120Chars -> PropertyOwner.EmailLengthIsGreaterThan120Chars
    DomainError.PropertyOwner.PaymentKeyLengthIsGreaterThan120Chars -> PropertyOwner.PaymentKeyLengthIsGreaterThan120Chars

    DomainError.PropertySeaProximity.PropertySeaProximityInvalid -> PropertySeaProximity.PropertySeaProximityInvalid
    DomainError.PropertySeaProximityDistanceToSea.Required -> PropertySeaProximityDistanceToSea.Required
    DomainError.PropertySeaProximityDistanceToSea.IsNegative -> PropertySeaProximityDistanceToSea.IsNegative

    DomainError.Stay.PropertyRequired -> Stay.PropertyRequired
    DomainError.Stay.TenantRequired -> Stay.TenantRequired
    DomainError.Stay.GuestsCountRequired -> Stay.GuestsCountRequired
    DomainError.Stay.GuestsCountIsNegative -> Stay.GuestsCountIsNegative
    DomainError.Stay.PeriodStartAndEndDatesAreTheSame -> Stay.PeriodStartAndEndDatesAreTheSame
    DomainError.Stay.PeriodStartDateIsAfterEndDate -> Stay.PeriodStartDateIsAfterEndDate
    DomainError.Stay.Overbooking -> Stay.Overbooking
    DomainError.Stay.CheckInDateRequired -> Stay.CheckInDateRequired
    DomainError.Stay.CheckInTimeRequired -> Stay.CheckInTimeRequired
    DomainError.Stay.CheckOutDateRequired -> Stay.CheckOutDateRequired
    DomainError.Stay.CheckOutTimeRequired -> Stay.CheckOutTimeRequired
    DomainError.Stay.IsAutoCheckInRequired -> Stay.IsAutoCheckInRequired
    DomainError.Stay.IsAutoCheckOutRequired -> Stay.IsAutoCheckOutRequired
    DomainError.Stay.TotalPriceIsNegative -> Stay.TotalPriceIsNegative
    DomainError.Stay.CommissionRequired -> Stay.CommissionRequired
    DomainError.Stay.CommissionIsNegative -> Stay.CommissionIsNegative
    DomainError.Stay.TransferDateRequired -> Stay.TransferDateRequired
    DomainError.Stay.TransferAmountRequired -> Stay.TransferAmountRequired
    DomainError.Stay.TransferAmountIsNegative -> Stay.TransferAmountIsNegative
    DomainError.Stay.StatusRequired -> Stay.StatusRequired
    DomainError.Stay.StatusInvalid -> Stay.StatusInvalid

    DomainError.Tenant.NameRequired -> Tenant.NameRequired
    DomainError.Tenant.NameBelongsToOtherTenant -> Tenant.NameBelongsToOtherTenant
    DomainError.Tenant.NameLengthIsGreaterThan100Chars -> Tenant.NameLengthIsGreaterThan100Chars
    DomainError.Tenant.PhoneLengthIsGreaterThan20Chars -> Tenant.PhoneLengthIsGreaterThan20Chars
    DomainError.Tenant.EmailInvalidFormat -> Tenant.EmailInvalidFormat
    DomainError.Tenant.EmailLengthIsGreaterThan120Chars -> Tenant.EmailLengthIsGreaterThan120Chars
    DomainError.Tenant.StatusInvalid -> Tenant.StatusInvalid

    DomainError.UserCreci.LengthIsGreaterThan8Chars -> UserCreci.LengthIsGreaterThan8Chars
}
