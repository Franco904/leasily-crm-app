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
import kotlinx.collections.immutable.toImmutableList

data class PropertyCompound(
    @Embedded val property: PropertyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = PropertyCharacteristicCrossRefEntity::class
    )
    val checkedCharacteristics: List<PropertyCharacteristicCrossRefEntity>,
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
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = StayEntity::class,
    )
    val stays: List<StayEntity>,
) {
    private val activeCharacteristics: List<PropertyCharacteristicCrossRef>
        get() {
            val activeCharacteristicIds = checkedCharacteristics
                .filter { !it.isDeleted }
                .map { it.propertyCharacteristicId }
                .toSet()

            return activeCharacteristicIds.map { activeCharacteristicId ->
                val characteristic = PropertyCharacteristic.parse(activeCharacteristicId)

                PropertyCharacteristicCrossRef(
                    createDateTime = property.createDateTime,
                    updateDateTime = property.updateDateTime,
                    propertyId = requireNotNull(property.id),
                    propertyCharacteristic = characteristic,
                )
            }
        }

    private val activePrices: List<PropertyPrice>
        get() = prices
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    private val activeFiles: List<PropertyFile>
        get() = files
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    private val activeStays: List<Long>
        get() = stays
            .filter { !it.isDeleted }
            .mapNotNull { it.id }

    fun toDomain() = Property.parse(
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
        characteristics = activeCharacteristics.toImmutableList(),
        prices = activePrices.toImmutableList(),
        files = activeFiles.toImmutableList(),
        staysIds = activeStays.toImmutableList(),
        inconsistencies = persistentListOf(), // property.inconsistencies,
        contractVersion = property.contractVersion,
    )
}
