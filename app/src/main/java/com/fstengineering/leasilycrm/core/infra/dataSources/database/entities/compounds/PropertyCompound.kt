package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristicProperty
import kotlinx.collections.immutable.toImmutableList

data class PropertyCompound(
    @Embedded val property: PropertyEntity,
    @Relation(
        parentColumn = "property_owner_id",
        entityColumn = "id",
        entity = PropertyOwnerEntity::class,
    )
    val owner: PropertyOwnerCompound,
    @Relation(
        parentColumn = "property_type_id",
        entityColumn = "id",
        entity = PropertyTypeEntity::class,
    )
    val type: PropertyTypeEntity,
    @Relation(
        parentColumn = "property_sea_proximity_type_id",
        entityColumn = "id",
        entity = PropertySeaProximityTypeEntity::class,
    )
    val seaProximityType: PropertySeaProximityTypeEntity?,
    @Relation(
        parentColumn = "id",
        entity = PropertyCharacteristicEntity::class,
        entityColumn = "id",
        associateBy = Junction(
            value = PropertyCharacteristicPropertyCrossRefEntity::class,
            parentColumn = "property_id",
            entityColumn = "property_characteristic_id"
        )
    )
    val allCharacteristics: List<PropertyCharacteristicEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = PropertyCharacteristicPropertyCrossRefEntity::class
    )
    val checkedCharacteristics: List<PropertyCharacteristicPropertyCrossRefEntity>,
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
    private val activeCharacteristics: List<PropertyCharacteristicProperty>
        get() {
            val activeCharacteristicIds = checkedCharacteristics
                .filter { !it.isDeleted }
                .map { it.propertyCharacteristicId }
                .toSet()

            return activeCharacteristicIds.map { activeCharacteristicId ->
                val characteristic = allCharacteristics.find { it.id == activeCharacteristicId }

                PropertyCharacteristicProperty(
                    propertyId = property.id,
                    propertyCharacteristic = characteristic?.toDomain(),
                    isDeleted = false,
                )
            }
        }

    private val activePrices: List<PropertyPriceEntity>
        get() = prices.filter { !it.isDeleted }

    private val activeFiles: List<PropertyFileEntity>
        get() = files.filter { !it.isDeleted }

    private val activeStays: List<StayEntity>
        get() = stays.filter { !it.isDeleted }

    fun toDomain() = Property(
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
        qtBedrooms = property.qtBedrooms,
        qtEnSuites = property.qtEnSuites,
        qtDoubleBeds = property.qtDoubleBeds,
        qtSingleBeds = property.qtSingleBeds,
        qtBathrooms = property.qtBathrooms,
        qtParkingSpots = property.qtParkingSpots,
        qtAirConditioners = property.qtAirConditioners,
        qtMaxGuests = property.qtMaxGuests,
        distanceToSea = property.distanceToSea,
        includedItems = property.includedItems,
        hasIncludedItems = property.hasIncludedItems,
        cleaningServiceTax = property.cleaningServiceTax,
        hasCleaningServiceIncluded = property.hasCleaningServiceIncluded,
        observations = property.observations,
        isDeleted = property.isDeleted,
        owner = owner.toDomain(),
        type = type.toDomain(),
        seaProximityType = seaProximityType?.toDomain(),
        characteristics = activeCharacteristics.toImmutableList(),
        prices = activePrices.map { it.toDomain() }.toImmutableList(),
        files = activeFiles.map { it.toDomain() }.toImmutableList(),
        stays = activeStays.map { it.toDomain() }.toImmutableList(),
        userId = property.userId,
    )
}
