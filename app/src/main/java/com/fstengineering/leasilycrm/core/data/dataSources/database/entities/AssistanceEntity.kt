package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity(
    tableName = "assistances",
    foreignKeys = [
        ForeignKey(
            entity = TenantEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenant_id"],
        ),
        ForeignKey(
            entity = PropertySeaProximityTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_sea_proximity_type_id"],
        ),
    ],
    indices = [
        Index("tenant_id"),
        Index("property_sea_proximity_type_id"),
    ],
)
data class AssistanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "contact_date") val contactDate: LocalDate?,
    @ColumnInfo(name = "searched_start_date") val searchedStartDate: LocalDate?,
    @ColumnInfo(name = "searched_end_date") val searchedEndDate: LocalDate?,
    @ColumnInfo(name = "has_flexibility_for_period") val hasFlexibilityForPeriod: Boolean,
    @ColumnInfo(name = "max_accepted_nightly_rate") val maxAcceptedNightlyRate: BigDecimal?,
    @ColumnInfo(name = "qt_adult_guests") val qtAdultGuests: Int,
    @ColumnInfo(name = "qt_kid_guests") val qtKidGuests: Int,
    @ColumnInfo(name = "qt_baby_guests") val qtBabyGuests: Int,
    @ColumnInfo(name = "qt_animal_guests") val qtAnimalGuests: Int,
    @ColumnInfo(name = "qt_bedrooms") val qtBedrooms: Int,
    @ColumnInfo(name = "qt_en_suites") val qtEnSuites: Int,
    @ColumnInfo(name = "qt_double_beds") val qtDoubleBeds: Int,
    @ColumnInfo(name = "qt_single_beds") val qtSingleBeds: Int,
    @ColumnInfo(name = "qt_bathrooms") val qtBathrooms: Int,
    @ColumnInfo(name = "qt_parking_spots") val qtParkingSpots: Int,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "tenant_id") val tenantId: Long,
    @ColumnInfo(name = "property_sea_proximity_type_id") val propertySeaProximityTypeId: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
)
