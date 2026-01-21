package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.application.models.AppDecimal
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Property(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
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
    val distanceToSea: AppDecimal? = null,
    val includedItems: String? = null,
    val hasIncludedItems: Boolean? = null,
    val cleaningServiceTax: AppDecimal? = null,
    val hasCleaningServiceIncluded: Boolean? = null,
    val observations: String? = null,
    val isDeleted: Boolean = false,
    val owner: PropertyOwner? = null,
    val type: PropertyType? = null,
    val seaProximityType: PropertySeaProximityType? = null,
    val characteristics: ImmutableList<PropertyCharacteristicProperty> = persistentListOf(),
    val prices: ImmutableList<PropertyPrice> = persistentListOf(),
    val files: ImmutableList<PropertyFile> = persistentListOf(),
    val stays: ImmutableList<Stay> = persistentListOf(),
    val userId: Long? = null,
) {
    val isAnyFormFieldInitialized = owner != null
            || type != null
            || neighborhood != null
            || streetAddress != null
            || cepPostalCode != null
            || number != null
            || addressLine2 != null
            || parkingSpotNumber != null
            || parkingBasementLevelNumber != null
            || hobbyBoxNumber != null
            || qtBedrooms > 0
            || qtEnSuites > 0
            || qtDoubleBeds > 0
            || qtSingleBeds > 0
            || qtBathrooms > 0
            || qtParkingSpots > 0
            || qtAirConditioners > 0
            || qtMaxGuests > 0
            || seaProximityType != null
            || includedItems != null
            || hasIncludedItems != null
            || characteristics.isNotEmpty()
            || cleaningServiceTax != null
            || hasCleaningServiceIncluded != null
            || prices.isNotEmpty()
            || files.isNotEmpty()
            || observations != null

}
