package com.fstengineering.leasilycrm.core.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

data class Property(
    val id: Int? = null,
    val registerDate: LocalDate? = null,
    val neighborhood: String? = null,
    val streetAddress: String? = null,
    val cepPostalCode: String? = null,
    val number: Int? = null,
    val addressLine2: String? = null,
    val parkingSpotNumber: String? = null,
    val parkingBasementLevelNumber: String? = null,
    val hobbyBoxNumber: String? = null,
    val qtBedrooms: Int? = null,
    val qtEnSuites: Int? = null,
    val qtDoubleBeds: Int? = null,
    val qtSingleBeds: Int? = null,
    val qtBathrooms: Int? = null,
    val qtParkingSpots: Int? = null,
    val qtAirConditioners: Int? = null,
    val qtMaxGuests: Int? = null,
    val includedItems: String? = null,
    val hasIncludedItems: Boolean? = null,
    val cleaningServiceTax: Double? = null,
    val hasCleaningServiceIncluded: Boolean? = null,
    val observations: String? = null,
    val isDeleted: Boolean = false,
    val owner: PropertyOwner? = null,
    val type: PropertyType? = null,
    val seaProximityType: PropertySeaProximityType? = null,
    val characteristics: ImmutableList<PropertyCharacteristicProperty> = persistentListOf(),
    val prices: ImmutableList<PropertyPrice> = persistentListOf(),
    val files: ImmutableList<PropertyFile> = persistentListOf(),
    val userId: Int? = null,
)
