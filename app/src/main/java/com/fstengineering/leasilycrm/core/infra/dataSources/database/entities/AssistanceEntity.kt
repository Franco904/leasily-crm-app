package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

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
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "searched_start_date") val searchedStartDate: AppDate?,
    @ColumnInfo(name = "searched_end_date") val searchedEndDate: AppDate?,
    @ColumnInfo(name = "has_flexibility_for_period") val hasFlexibilityForPeriod: Boolean,
    @ColumnInfo(name = "max_accepted_nightly_rate") val maxAcceptedNightlyRate: AppDecimal?,
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
    @ColumnInfo(name = "distance_to_sea") val distanceToSea: AppDecimal?,
    val observations: String?,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "property_sea_proximity_type_id") val propertySeaProximityTypeId: Long?,
    @ColumnInfo(name = "tenant_id") val tenantId: Long,
) {
    companion object {
        fun fromDomain(domain: Assistance) =
            AssistanceEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                searchedStartDate = domain.searchedStartDate,
                searchedEndDate = domain.searchedEndDate,
                hasFlexibilityForPeriod = domain.hasFlexibilityForPeriod,
                maxAcceptedNightlyRate = domain.maxAcceptedNightlyRate,
                qtAdultGuests = domain.qtAdultGuests,
                qtKidGuests = domain.qtKidGuests,
                qtBabyGuests = domain.qtBabyGuests,
                qtAnimalGuests = domain.qtAnimalGuests,
                qtBedrooms = domain.qtBedrooms,
                qtEnSuites = domain.qtEnSuites,
                qtDoubleBeds = domain.qtDoubleBeds,
                qtSingleBeds = domain.qtSingleBeds,
                qtBathrooms = domain.qtBathrooms,
                qtParkingSpots = domain.qtParkingSpots,
                distanceToSea = domain.distanceToSea,
                observations = domain.observations,
                isFinished = domain.isFinished,
                isDeleted = domain.isDeleted,
                propertySeaProximityTypeId = domain.seaProximityType?.id,
                tenantId = domain.tenantId,
            )
    }
}
