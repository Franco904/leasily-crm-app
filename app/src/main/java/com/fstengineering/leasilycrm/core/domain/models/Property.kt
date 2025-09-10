package com.fstengineering.leasilycrm.core.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal
import java.time.LocalDate

data class Property(
    val id: Long? = null,
    val registerDate: LocalDate? = null,
    val neighborhood: String? = null,
    val streetAddress: String? = null,
    val cepPostalCode: String? = null,
    val number: Int? = null,
    val addressLine2: String? = null,
    val parkingSpotNumber: String? = null,
    val parkingBasementLevelNumber: String? = null,
    val hobbyBoxNumber: String? = null,
    val qtBedrooms: Int = 1,
    val qtEnSuites: Int = 0,
    val qtDoubleBeds: Int = 0,
    val qtSingleBeds: Int = 0,
    val qtBathrooms: Int = 0,
    val qtParkingSpots: Int = 0,
    val qtAirConditioners: Int = 0,
    val qtMaxGuests: Int = 0,
    val distanceToSea: BigDecimal? = null,
    val includedItems: String? = null,
    val hasIncludedItems: Boolean? = null,
    val cleaningServiceTax: BigDecimal? = null,
    val hasCleaningServiceIncluded: Boolean? = null,
    val observations: String? = null,
    val isDeleted: Boolean = false,
    val owner: PropertyOwner? = null,
    val type: PropertyType? = null,
    val seaProximityType: PropertySeaProximityType? = null,
    val characteristics: ImmutableList<PropertyCharacteristicProperty> = persistentListOf(),
    val prices: ImmutableList<PropertyPrice> = persistentListOf(),
    val files: ImmutableList<PropertyFile> = persistentListOf(),
    val userId: Long? = null,
)
