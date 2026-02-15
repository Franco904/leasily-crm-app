package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import kotlinx.collections.immutable.ImmutableList

data class Property(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val owner: PropertyOwner,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val cepPostalCode: String?,
    val number: Int?,
    val addressLine2: String?,
    val parkingSpotNumber: String?,
    val parkingBasementLevelNumber: String?,
    val hobbyBoxNumber: String?,
    val qtBedrooms: Int,
    val qtEnSuites: Int,
    val qtDoubleBeds: Int,
    val qtSingleBeds: Int,
    val qtBathrooms: Int,
    val qtParkingSpots: Int,
    val qtAirConditioners: Int,
    val qtMaxGuests: Int,
    val seaProximityType: PropertySeaProximityType?,
    val distanceToSea: AppDecimal?,
    val includedItems: String?,
    val hasIncludedItems: Boolean,
    val characteristics: ImmutableList<PropertyCharacteristicProperty>,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean,
    val prices: ImmutableList<PropertyPrice>,
    val files: ImmutableList<PropertyFile>,
    val observations: String?,
    val isDeleted: Boolean,
    val stays: ImmutableList<Stay>,
    val userId: Long,
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
