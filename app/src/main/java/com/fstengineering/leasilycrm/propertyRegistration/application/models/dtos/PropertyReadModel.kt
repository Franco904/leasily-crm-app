package com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import kotlinx.collections.immutable.ImmutableList

data class PropertyReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val owner: PropertyOwnerReadModel?,
    val type: PropertyType?,
    val neighborhood: String?,
    val streetAddress: String?,
    val cepPostalCode: String?,
    val number: Int?,
    val addressLine2: String?,
    val parkingSpotNumber: String?,
    val parkingBasementLevelNumber: String?,
    val hobbyBoxNumber: String?,
    val bedroomsCount: Int?,
    val enSuitesCount: Int?,
    val doubleBedsCount: Int?,
    val singleBedsCount: Int?,
    val bathroomsCount: Int?,
    val parkingSpotsCount: Int?,
    val airConditionersCount: Int?,
    val maxGuestsCount: Int?,
    val seaProximityTypeId: Long?,
    val distanceToSea: AppDecimal?,
    val includedItems: String?,
    val hasIncludedItems: Boolean?,
    val characteristics: ImmutableList<PropertyCharacteristic>?,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean?,
    val prices: ImmutableList<PropertyPriceReadModel>?,
    val files: ImmutableList<PropertyFileReadModel>?,
    val observations: String?,
) {
    companion object {
        fun create(
            propertyRaw: PropertyRawReadModel?,
            owner: PropertyOwnerReadModel?,
        ) = PropertyReadModel(
            id = propertyRaw?.id,
            createDateTime = propertyRaw?.createDateTime,
            owner = owner,
            type = propertyRaw?.type,
            neighborhood = propertyRaw?.neighborhood,
            streetAddress = propertyRaw?.streetAddress,
            cepPostalCode = propertyRaw?.cepPostalCode,
            number = propertyRaw?.number,
            addressLine2 = propertyRaw?.addressLine2,
            parkingSpotNumber = propertyRaw?.parkingSpotNumber,
            parkingBasementLevelNumber = propertyRaw?.parkingBasementLevelNumber,
            hobbyBoxNumber = propertyRaw?.hobbyBoxNumber,
            bedroomsCount = propertyRaw?.bedroomsCount,
            enSuitesCount = propertyRaw?.enSuitesCount,
            doubleBedsCount = propertyRaw?.doubleBedsCount,
            singleBedsCount = propertyRaw?.singleBedsCount,
            bathroomsCount = propertyRaw?.bathroomsCount,
            parkingSpotsCount = propertyRaw?.parkingSpotsCount,
            airConditionersCount = propertyRaw?.airConditionersCount,
            maxGuestsCount = propertyRaw?.maxGuestsCount,
            seaProximityTypeId = propertyRaw?.seaProximityTypeId,
            distanceToSea = propertyRaw?.distanceToSea,
            includedItems = propertyRaw?.includedItems,
            hasIncludedItems = propertyRaw?.hasIncludedItems,
            characteristics = propertyRaw?.characteristics,
            cleaningServiceTax = propertyRaw?.cleaningServiceTax,
            hasCleaningServiceIncluded = propertyRaw?.hasCleaningServiceIncluded,
            prices = propertyRaw?.prices,
            files = propertyRaw?.files,
            observations = propertyRaw?.observations,
        )
    }
}
