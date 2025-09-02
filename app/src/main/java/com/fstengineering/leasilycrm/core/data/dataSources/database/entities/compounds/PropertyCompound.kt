package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.views.PropertyCharacteristicNotDeletedView
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
        entityColumn = "id",
        entity = PropertyCharacteristicNotDeletedView::class,
        associateBy = Junction(
            value = PropertyCharacteristicPropertyCrossRefEntity::class,
            parentColumn = "property_id",
            entityColumn = "property_characteristic_id",
        ),
    )
    val characteristics: List<PropertyCharacteristicNotDeletedView>,
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
    val activePrices: List<PropertyPriceEntity>
        get() = prices.filter { !it.isDeleted }

    val activeFiles: List<PropertyFileEntity>
        get() = files.filter { !it.isDeleted }

    fun toDomain() = Property(
        id = property.id,
        registerDate = property.registerDate,
        streetAddress = property.streetAddress,
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
        includedItems = property.includedItems,
        hasIncludedItems = property.hasIncludedItems,
        cleaningServiceTax = property.cleaningServiceTax,
        hasCleaningServiceIncluded = property.hasCleaningServiceIncluded,
        observations = property.observations,
        isDeleted = property.isDeleted,
        owner = owner.toDomain(),
        type = type.toDomain(),
        seaProximityType = seaProximityType?.toDomain(),
        characteristics = characteristics.map { characteristic ->
            PropertyCharacteristicProperty(
                propertyId = property.id,
                propertyCharacteristic = characteristic.toDomain(),
                isDeleted = false,
            )
        }.toImmutableList(),
        prices = activePrices.map { it.toDomain() }.toImmutableList(),
        files = activeFiles.map { it.toDomain() }.toImmutableList(),
        userId = property.userId,
    )
}
