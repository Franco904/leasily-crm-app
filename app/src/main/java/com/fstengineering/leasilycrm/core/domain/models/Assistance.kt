package com.fstengineering.leasilycrm.core.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal
import java.time.LocalDate

data class Assistance(
    val id: Long? = null,
    val searchedStartDate: LocalDate? = null,
    val searchedEndDate: LocalDate? = null,
    val hasFlexibilityForPeriod: Boolean? = null,
    val maxAcceptedNightlyRate: BigDecimal? = null,
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
    val isDeleted: Boolean = false,
    val searchedNeighborhoods: ImmutableList<AssistanceSearchedNeighborhood> = persistentListOf(),
    val seaProximityType: PropertySeaProximityType? = null,
    val characteristics: ImmutableList<AssistanceCharacteristicAssistance> = persistentListOf(),
    val observations: String? = null,
    val sentProperties: ImmutableList<AssistanceSentProperty> = persistentListOf(),
    val interactions: ImmutableList<AssistanceInteraction> = persistentListOf(),
    val tenant: Tenant? = null,
)
