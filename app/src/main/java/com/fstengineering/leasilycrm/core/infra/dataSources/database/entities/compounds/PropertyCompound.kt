package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.Property
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.PropertyFile
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.PropertyPrice
import kotlinx.collections.immutable.toPersistentList

data class PropertyCompound(
    @Embedded val property: PropertyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = PropertyCharacteristicCrossRefEntity::class
    )
    val characteristics: List<PropertyCharacteristicCrossRefEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = PropertyPriceEntity::class,
    )
    val prices: List<PropertyPriceEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = PropertyFileEntity::class,
    )
    val files: List<PropertyFileEntity>,
) {
    fun toDomain(): Property {
        val characteristics = characteristics
            .mapNotNull { PropertyCharacteristic.fromId(it.propertyCharacteristicId) }
            .toPersistentList()

        val domainPrices = prices.mapNotNull { price ->
            PropertyPrice.parse(
                id = price.id,
                startDate = price.startDate,
                endDate = price.endDate,
                amount = price.amount,
            ).fold(onErrors = { null }, onSuccess = { it })
        }.toPersistentList()

        val domainFiles = files.map { file ->
            PropertyFile(id = file.id, uriString = file.propertyFileUri, isMainPicture = file.isMainPicture)
        }.toPersistentList()

        return Property.parse(
            id = property.id,
            createDateTime = property.createDateTime,
            ownerId = property.ownerId,
            type = PropertyType.fromId(property.typeId),
            neighborhood = property.neighborhood,
            streetAddress = property.streetAddress,
            cepPostalCode = property.cepPostalCode,
            number = property.number,
            addressLine2 = property.addressLine2,
            parkingSpotNumber = property.parkingSpotNumber,
            parkingBasementLevelNumber = property.parkingBasementLevelNumber,
            hobbyBoxNumber = property.hobbyBoxNumber,
            bedroomsCount = property.bedrooms,
            enSuitesCount = property.enSuites,
            doubleBedsCount = property.doubleBeds,
            singleBedsCount = property.singleBeds,
            bathroomsCount = property.bathrooms,
            parkingSpotsCount = property.parkingSpots,
            airConditionersCount = property.airConditioners,
            maxGuestsCount = property.maxGuests,
            seaProximityTypeId = property.seaProximityTypeId,
            distanceToSea = property.distanceToSea,
            includedItems = property.includedItems,
            hasIncludedItems = property.hasIncludedItems,
            cleaningServiceTax = property.cleaningServiceTax,
            hasCleaningServiceIncluded = property.hasCleaningServiceIncluded,
            observations = property.observations,
            characteristics = characteristics,
            prices = domainPrices,
            files = domainFiles,
        ).fold(
            onErrors = { errors -> throw IllegalStateException("Failed to parse Property from DB: $errors") },
            onSuccess = { it },
        )
    }
}
