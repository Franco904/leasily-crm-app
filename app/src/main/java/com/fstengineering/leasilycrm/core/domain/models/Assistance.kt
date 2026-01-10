package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.application.models.AppDecimal
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Assistance(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val searchedStartDate: AppDate? = null,
    val searchedEndDate: AppDate? = null,
    val hasFlexibilityForPeriod: Boolean = false,
    val maxAcceptedNightlyRate: AppDecimal? = null,
    val qtAdultGuests: Int = 0,
    val qtKidGuests: Int = 0,
    val qtBabyGuests: Int = 0,
    val qtAnimalGuests: Int = 0,
    val qtBedrooms: Int = 0,
    val qtEnSuites: Int = 0,
    val qtDoubleBeds: Int = 0,
    val qtSingleBeds: Int = 0,
    val qtBathrooms: Int = 0,
    val qtParkingSpots: Int = 0,
    val seaProximityType: PropertySeaProximityType? = null,
    val distanceToSea: AppDecimal? = null,
    val isFinished: Boolean = false,
    val isDeleted: Boolean = false,
    val searchedNeighborhoods: ImmutableList<AssistanceSearchedNeighborhood> = persistentListOf(),
    val characteristics: ImmutableList<AssistanceCharacteristicAssistance> = persistentListOf(),
    val observations: String? = null,
    val sentProperties: ImmutableList<AssistanceSentProperty> = persistentListOf(),
    val interactions: ImmutableList<AssistanceInteraction> = persistentListOf(),
    val tenant: Tenant? = null,
) {
    val qtGuests get() = qtAdultGuests + qtKidGuests + qtBabyGuests + qtAnimalGuests
}
