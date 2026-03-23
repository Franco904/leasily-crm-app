package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.entities.PropertyFile
import com.fstengineering.leasilycrm.core.domain.entities.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import kotlinx.collections.immutable.persistentListOf
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

        return Property.parse(
            id = property.id,
            createDateTime = property.createDateTime,
            updateDateTime = property.updateDateTime,
            neighborhood = property.neighborhood,
            streetAddress = property.streetAddress,
            cepPostalCode = property.cepPostalCode,
            number = property.number,
            addressLine2 = property.addressLine2,
            parkingSpotNumber = property.parkingSpotNumber,
            parkingBasementLevelNumber = property.parkingBasementLevelNumber,
            hobbyBoxNumber = property.hobbyBoxNumber,
            bedrooms = property.bedrooms,
            enSuites = property.enSuites,
            doubleBeds = property.doubleBeds,
            singleBeds = property.singleBeds,
            bathrooms = property.bathrooms,
            parkingSpots = property.parkingSpots,
            airConditioners = property.airConditioners,
            maxGuests = property.maxGuests,
            distanceToSea = property.distanceToSea,
            includedItems = property.includedItems,
            hasIncludedItems = property.hasIncludedItems,
            cleaningServiceTax = property.cleaningServiceTax,
            hasCleaningServiceIncluded = property.hasCleaningServiceIncluded,
            observations = property.observations,
            ownerId = property.ownerId,
            typeId = property.typeId,
            seaProximityTypeId = property.seaProximityTypeId,
            characteristics = characteristics.toPersistentList(),
            prices = activePrices.toPersistentList(),
            files = activeFiles.toPersistentList(),
            staysIds = activeStays.toPersistentList(),
            inconsistencies = persistentListOf(), // property.inconsistencies,
            contractVersion = property.contractVersion,
        )
    }
}
