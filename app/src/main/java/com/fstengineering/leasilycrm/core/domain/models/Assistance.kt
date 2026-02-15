package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import kotlinx.collections.immutable.ImmutableList

data class Assistance(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val searchedStartDate: AppDate?,
    val searchedEndDate: AppDate?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val qtAdultGuests: Int,
    val qtKidGuests: Int,
    val qtBabyGuests: Int,
    val qtAnimalGuests: Int,
    val qtBedrooms: Int,
    val qtEnSuites: Int,
    val qtDoubleBeds: Int,
    val qtSingleBeds: Int,
    val qtBathrooms: Int,
    val qtParkingSpots: Int,
    val seaProximityType: PropertySeaProximityType?,
    val distanceToSea: AppDecimal?,
    val isFinished: Boolean,
    val isDeleted: Boolean,
    val searchedNeighborhoods: ImmutableList<AssistanceSearchedNeighborhood>,
    val characteristics: ImmutableList<AssistanceCharacteristicAssistance>,
    val observations: String?,
    val sentProperties: ImmutableList<AssistanceSentProperty>,
    val interactions: ImmutableList<AssistanceInteraction>,
    val tenantId: Long,
) {
    val qtGuests get() = qtAdultGuests + qtKidGuests + qtBabyGuests + qtAnimalGuests

    val isAnyFormFieldInitialized = searchedStartDate != null
            || searchedEndDate != null
            || hasFlexibilityForPeriod
            || maxAcceptedNightlyRate != null
            || qtAdultGuests > 0
            || qtKidGuests > 0
            || qtBabyGuests > 0
            || qtAnimalGuests > 0
            || qtBedrooms > 0
            || qtEnSuites > 0
            || qtDoubleBeds > 0
            || qtSingleBeds > 0
            || qtBathrooms > 0
            || qtParkingSpots > 0
            || seaProximityType != null
            || distanceToSea != null
            || searchedNeighborhoods != null
            || characteristics != null
            || observations != null
            || sentProperties != null
            || interactions != null
}
