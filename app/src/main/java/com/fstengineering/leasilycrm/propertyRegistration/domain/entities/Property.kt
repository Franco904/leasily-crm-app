package com.fstengineering.leasilycrm.propertyRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.domain.valueObjects.Quantidade
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class Property private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val ownerId: Long,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val cepPostalCode: String?,
    val number: Int?,
    val addressLine2: String?,
    val parkingSpotNumber: String?,
    val parkingBasementLevelNumber: String?,
    val hobbyBoxNumber: String?,
    val bedroomsCount: Quantidade,
    val enSuitesCount: Quantidade,
    val doubleBedsCount: Quantidade,
    val singleBedsCount: Quantidade,
    val bathroomsCount: Quantidade,
    val parkingSpotsCount: Quantidade,
    val airConditionersCount: Quantidade,
    val maxGuestsCount: Quantidade,
    val seaProximityType: PropertySeaProximityType?,
    val includedItems: String?,
    val hasIncludedItems: Boolean,
    val characteristics: ImmutableList<PropertyCharacteristic>,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean,
    val prices: ImmutableList<PropertyPrice>,
    val files: ImmutableList<PropertyFile>,
    val observations: String?,
) {
    companion object {
        fun parse(
            id: Long? = null,
            createDateTime: AppDateTime? = null,
            ownerId: Long? = null,
            type: PropertyType? = null,
            neighborhood: String? = null,
            streetAddress: String? = null,
            cepPostalCode: String? = null,
            number: Int? = null,
            addressLine2: String? = null,
            parkingSpotNumber: String? = null,
            parkingBasementLevelNumber: String? = null,
            hobbyBoxNumber: String? = null,
            bedroomsCount: Int = 0,
            enSuitesCount: Int = 0,
            doubleBedsCount: Int = 0,
            singleBedsCount: Int = 0,
            bathroomsCount: Int = 0,
            parkingSpotsCount: Int = 0,
            airConditionersCount: Int = 0,
            maxGuestsCount: Int = 0,
            seaProximityTypeId: Long? = null,
            distanceToSea: AppDecimal? = null,
            includedItems: String? = null,
            hasIncludedItems: Boolean = false,
            characteristics: ImmutableList<PropertyCharacteristic> = persistentListOf(),
            cleaningServiceTax: AppDecimal? = null,
            hasCleaningServiceIncluded: Boolean = false,
            prices: ImmutableList<PropertyPrice> = persistentListOf(),
            files: ImmutableList<PropertyFile> = persistentListOf(),
            observations: String? = null,
        ): AppResult<Property, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (ownerId == null) errors.add(DomainError.PropertyPropertyOwner.Required)
            if (type == null) errors.add(DomainError.PropertyType.Required)

            if (neighborhood.isNullOrBlank()) errors.add(DomainError.Neighborhood.Required)
            if (streetAddress.isNullOrBlank()) errors.add(DomainError.PropertyStreetAddress.Required)

            val seaProximityType = seaProximityTypeId?.let { typeId ->
                PropertySeaProximityType.parse(typeId, distanceToSea = distanceToSea)
                    .fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })
            }

            if (files.count { it.isMainPicture } > 1) {
                errors.add(DomainError.PropertyOtherFields.MoreThanOneMainPictureFile)
            }

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = Property(
                id = id,
                createDateTime = createDateTime,
                ownerId = requireNotNull(ownerId),
                type = requireNotNull(type),
                neighborhood = requireNotNull(neighborhood),
                streetAddress = requireNotNull(streetAddress),
                cepPostalCode = cepPostalCode,
                number = number,
                addressLine2 = addressLine2,
                parkingSpotNumber = parkingSpotNumber,
                parkingBasementLevelNumber = parkingBasementLevelNumber,
                hobbyBoxNumber = hobbyBoxNumber,
                bedroomsCount = Quantidade.of(bedroomsCount),
                enSuitesCount = Quantidade.of(enSuitesCount),
                doubleBedsCount = Quantidade.of(doubleBedsCount),
                singleBedsCount = Quantidade.of(singleBedsCount),
                bathroomsCount = Quantidade.of(bathroomsCount),
                parkingSpotsCount = Quantidade.of(parkingSpotsCount),
                airConditionersCount = Quantidade.of(airConditionersCount),
                maxGuestsCount = Quantidade.of(maxGuestsCount),
                seaProximityType = seaProximityType,
                includedItems = includedItems,
                hasIncludedItems = hasIncludedItems,
                characteristics = characteristics,
                cleaningServiceTax = cleaningServiceTax,
                hasCleaningServiceIncluded = hasCleaningServiceIncluded,
                prices = prices,
                files = files,
                observations = observations,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
