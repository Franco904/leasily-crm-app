package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
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
    val bedrooms: Int,
    val enSuites: Int,
    val doubleBeds: Int,
    val singleBeds: Int,
    val bathrooms: Int,
    val parkingSpots: Int,
    val airConditioners: Int,
    val maxGuests: Int,
    val seaProximityType: PropertySeaProximityType?,
    val includedItems: String?,
    val hasIncludedItems: Boolean,
    val characteristics: ImmutableList<PropertyCharacteristicCrossRef>,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean,
    val prices: ImmutableList<PropertyPrice>,
    val files: ImmutableList<PropertyFile>,
    val observations: String?,
    val stays: ImmutableList<Stay>,
    val inconsistencies: String?,
    val contractVersion: Int,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            owner: PropertyOwner?,
            typeId: Long?,
            neighborhood: String?,
            streetAddress: String?,
            cepPostalCode: String?,
            number: Int?,
            addressLine2: String?,
            parkingSpotNumber: String?,
            parkingBasementLevelNumber: String?,
            hobbyBoxNumber: String?,
            bedrooms: Int,
            enSuites: Int,
            doubleBeds: Int,
            singleBeds: Int,
            bathrooms: Int,
            parkingSpots: Int,
            airConditioners: Int,
            maxGuests: Int,
            seaProximityTypeId: Long?,
            distanceToSea: AppDecimal?,
            includedItems: String?,
            hasIncludedItems: Boolean,
            characteristics: ImmutableList<PropertyCharacteristicCrossRef>,
            cleaningServiceTax: AppDecimal?,
            hasCleaningServiceIncluded: Boolean,
            prices: ImmutableList<PropertyPrice>,
            files: ImmutableList<PropertyFile>,
            observations: String?,
            stays: ImmutableList<Stay>,
            inconsistencies: String?,
            contractVersion: Int,
        ): Property {
            validateOwner(owner?.id).onError { throw it.asException() }
            validateType(typeId).onError { throw it.asException() }
            validateNeighborhood(neighborhood).onError { throw it.asException() }
            validateStreetAddress(streetAddress).onError { throw it.asException() }

            val type = PropertyType.parse(requireNotNull(typeId))

            if (neighborhood.isNullOrBlank()) throw DomainError.Property.NeighborhoodRequired.asException()
            if (streetAddress.isNullOrBlank()) throw DomainError.Property.StreetAddressRequired.asException()

            val seaProximityType = seaProximityTypeId?.let {
                PropertySeaProximityType.parse(it, distanceToSea = distanceToSea)
            }

            return Property(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                owner = requireNotNull(owner),
                type = type,
                neighborhood = neighborhood,
                streetAddress = streetAddress,
                cepPostalCode = cepPostalCode,
                number = number,
                addressLine2 = addressLine2,
                parkingSpotNumber = parkingSpotNumber,
                parkingBasementLevelNumber = parkingBasementLevelNumber,
                hobbyBoxNumber = hobbyBoxNumber,
                bedrooms = bedrooms,
                enSuites = enSuites,
                doubleBeds = doubleBeds,
                singleBeds = singleBeds,
                bathrooms = bathrooms,
                parkingSpots = parkingSpots,
                airConditioners = airConditioners,
                maxGuests = maxGuests,
                seaProximityType = seaProximityType,
                includedItems = includedItems,
                hasIncludedItems = hasIncludedItems,
                characteristics = characteristics,
                cleaningServiceTax = cleaningServiceTax,
                hasCleaningServiceIncluded = hasCleaningServiceIncluded,
                prices = prices,
                files = files,
                observations = observations,
                stays = stays,
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )
        }

        fun validateOwner(ownerId: Long?): AppResult<Unit, DomainError.Property> {
            return when {
                ownerId == null -> AppResult.Error(DomainError.Property.OwnerRequired)
                else -> AppResult.Success(Unit)
            }
        }

        fun validateType(typeId: Long?): AppResult<Unit, DomainError.Property> {
            return when {
                typeId == null -> AppResult.Error(DomainError.Property.TypeRequired)
                else -> AppResult.Success(Unit)
            }
        }

        fun validateNeighborhood(neighborhood: String?): AppResult<Unit, DomainError.Property> {
            return when {
                neighborhood.isNullOrBlank() -> AppResult.Error(DomainError.Property.NeighborhoodRequired)
                else -> AppResult.Success(Unit)
            }
        }

        fun validateStreetAddress(streetAddress: String?): AppResult<Unit, DomainError.Property> {
            return when {
                streetAddress.isNullOrBlank() -> AppResult.Error(DomainError.Property.StreetAddressRequired)
                else -> AppResult.Success(Unit)
            }
        }
    }
}

// val isAnyFormFieldInitialized = owner != null
//            || type != null
//            || neighborhood != null
//            || streetAddress != null
//            || cepPostalCode != null
//            || number != null
//            || addressLine2 != null
//            || parkingSpotNumber != null
//            || parkingBasementLevelNumber != null
//            || hobbyBoxNumber != null
//            || qtBedrooms > 0
//            || qtEnSuites > 0
//            || qtDoubleBeds > 0
//            || qtSingleBeds > 0
//            || qtBathrooms > 0
//            || qtParkingSpots > 0
//            || qtAirConditioners > 0
//            || qtMaxGuests > 0
//            || seaProximityType != null
//            || includedItems != null
//            || hasIncludedItems != null
//            || characteristics.isNotEmpty()
//            || cleaningServiceTax != null
//            || hasCleaningServiceIncluded != null
//            || prices.isNotEmpty()
//            || files.isNotEmpty()
//            || observations != null
