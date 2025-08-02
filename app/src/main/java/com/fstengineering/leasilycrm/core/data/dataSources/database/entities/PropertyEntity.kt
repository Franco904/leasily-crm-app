package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "properties",
    foreignKeys = [
        ForeignKey(
            entity = PropertyOwnerEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_owner_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PropertyTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_type_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PropertySeaProximityTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_sea_proximity_type_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("property_owner_id"),
        Index("property_type_id"),
        Index("property_sea_proximity_type_id"),
    ],
)
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "register_date") val registerDate: LocalDate,
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
    @ColumnInfo(name = "included_items") val includedItems: String?,
    @ColumnInfo(name = "has_included_items") val hasIncludedItems: Boolean,
    @ColumnInfo(name = "cleaning_service_tax") val cleaningServiceTax: Double?,
    @ColumnInfo(name = "has_cleaning_service_included") val hasCleaningServiceIncluded: Boolean,
    val observations: String?,
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Int,
    @ColumnInfo(name = "property_type_id") val propertyTypeId: Int,
    @ColumnInfo(name = "property_sea_proximity_type_id") val propertySeaProximityType: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
)
