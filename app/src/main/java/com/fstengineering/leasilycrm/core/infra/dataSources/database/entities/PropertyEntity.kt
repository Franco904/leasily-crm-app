package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

@Entity(
    tableName = "properties",
    foreignKeys = [
        ForeignKey(
            entity = PropertyOwnerEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_owner_id"],
        ),
        ForeignKey(
            entity = PropertyTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_type_id"],
        ),
        ForeignKey(
            entity = PropertySeaProximityTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_sea_proximity_type_id"],
        )
    ],
    indices = [
        Index("property_owner_id"),
        Index("property_type_id"),
        Index("property_sea_proximity_type_id"),
        Index("user_id"),
    ],
)
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val neighborhood: String,
    @ColumnInfo(name = "street_address") val streetAddress: String,
    @ColumnInfo(name = "cep_postal_code") val cepPostalCode: String?,
    val number: Int?,
    @ColumnInfo(name = "address_line_2") val addressLine2: String?,
    @ColumnInfo(name = "parking_spot_number") val parkingSpotNumber: String?,
    @ColumnInfo(name = "parking_basement_level_number") val parkingBasementLevelNumber: String?,
    @ColumnInfo(name = "hobby_box_number") val hobbyBoxNumber: String?,
    @ColumnInfo(name = "qt_bedrooms") val qtBedrooms: Int,
    @ColumnInfo(name = "qt_en_suites") val qtEnSuites: Int,
    @ColumnInfo(name = "qt_double_beds") val qtDoubleBeds: Int,
    @ColumnInfo(name = "qt_single_beds") val qtSingleBeds: Int,
    @ColumnInfo(name = "qt_bathrooms") val qtBathrooms: Int,
    @ColumnInfo(name = "qt_parking_spots") val qtParkingSpots: Int,
    @ColumnInfo(name = "qt_air_conditioners") val qtAirConditioners: Int,
    @ColumnInfo(name = "qt_max_guests") val qtMaxGuests: Int,
    @ColumnInfo(name = "distance_to_sea") val distanceToSea: AppDecimal?,
    @ColumnInfo(name = "included_items") val includedItems: String?,
    @ColumnInfo(name = "has_included_items") val hasIncludedItems: Boolean,
    @ColumnInfo(name = "cleaning_service_tax") val cleaningServiceTax: AppDecimal?,
    @ColumnInfo(name = "has_cleaning_service_included") val hasCleaningServiceIncluded: Boolean,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Long,
    @ColumnInfo(name = "property_type_id") val propertyTypeId: Long,
    @ColumnInfo(name = "property_sea_proximity_type_id") val propertySeaProximityType: Long?,
    @ColumnInfo(name = "user_id") val userId: Long,
) {
    companion object {
        fun fromDomain(domain: Property) =
            PropertyEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                neighborhood = domain.neighborhood,
                streetAddress = domain.streetAddress,
                cepPostalCode = domain.cepPostalCode,
                number = domain.number,
                addressLine2 = domain.addressLine2,
                parkingSpotNumber = domain.parkingSpotNumber,
                parkingBasementLevelNumber = domain.parkingBasementLevelNumber,
                hobbyBoxNumber = domain.hobbyBoxNumber,
                qtBedrooms = domain.qtBedrooms,
                qtEnSuites = domain.qtEnSuites,
                qtDoubleBeds = domain.qtDoubleBeds,
                qtSingleBeds = domain.qtSingleBeds,
                qtBathrooms = domain.qtBathrooms,
                qtParkingSpots = domain.qtParkingSpots,
                qtAirConditioners = domain.qtAirConditioners,
                qtMaxGuests = domain.qtMaxGuests,
                distanceToSea = domain.distanceToSea,
                includedItems = domain.includedItems,
                hasIncludedItems = domain.hasIncludedItems,
                cleaningServiceTax = domain.cleaningServiceTax,
                hasCleaningServiceIncluded = domain.hasCleaningServiceIncluded,
                observations = domain.observations,
                isDeleted = domain.isDeleted,
                propertyOwnerId = requireNotNull(domain.owner.id),
                propertyTypeId = domain.type.id,
                propertySeaProximityType = domain.seaProximityType?.id,
                userId = domain.userId,
            )
    }
}
