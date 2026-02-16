package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal

@Entity(
    tableName = "assistances",
    foreignKeys = [
        ForeignKey(
            entity = TenantEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenant_id"],
        ),
    ],
    indices = [
        Index("tenant_id"),
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
    @ColumnInfo(name = "adults") val adults: Int,
    @ColumnInfo(name = "kids") val kids: Int,
    @ColumnInfo(name = "babies") val babies: Int,
    @ColumnInfo(name = "animals") val animals: Int,
    @ColumnInfo(name = "bedrooms") val bedrooms: Int,
    @ColumnInfo(name = "en_suites") val enSuites: Int,
    @ColumnInfo(name = "double_beds") val doubleBeds: Int,
    @ColumnInfo(name = "single_beds") val singleBeds: Int,
    @ColumnInfo(name = "bathrooms") val bathrooms: Int,
    @ColumnInfo(name = "parking_spots") val parkingSpots: Int,
    @ColumnInfo(name = "distance_to_sea") val distanceToSea: AppDecimal?,
    val observations: String?,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "property_sea_proximity_type_id") val propertySeaProximityTypeId: Long?,
    @ColumnInfo(name = "tenant_id") val tenantId: Long,
    val inconsistencies: String?,
    @ColumnInfo(name = "contract_version") val contractVersion: Int,
) {
    companion object {
        fun fromDomain(domain: Assistance): AssistanceEntity {
            val seaProximityType = domain.seaProximityType

            return AssistanceEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                searchedStartDate = domain.searchedPeriod?.startDate,
                searchedEndDate = domain.searchedPeriod?.endDate,
                hasFlexibilityForPeriod = domain.hasFlexibilityForPeriod,
                maxAcceptedNightlyRate = domain.maxAcceptedNightlyRate,
                adults = domain.guestsCount.adults,
                kids = domain.guestsCount.kids,
                babies = domain.guestsCount.babies,
                animals = domain.guestsCount.animals,
                bedrooms = domain.preferredFeaturesCount.bedrooms,
                enSuites = domain.preferredFeaturesCount.enSuites,
                doubleBeds = domain.preferredFeaturesCount.doubleBeds,
                singleBeds = domain.preferredFeaturesCount.singleBeds,
                bathrooms = domain.preferredFeaturesCount.bathrooms,
                parkingSpots = domain.preferredFeaturesCount.parkingSpots,
                propertySeaProximityTypeId = seaProximityType?.id,
                distanceToSea = if (seaProximityType is PropertySeaProximityType.DistanceToSea) {
                    seaProximityType.distance
                } else null,
                observations = domain.observations,
                isFinished = domain.isFinished,
                isDeleted = domain.isDeleted,
                tenantId = domain.tenantId,
                inconsistencies = domain.inconsistencies,
                contractVersion = BuildConfig.CONTRACT_VERSION,
            )
        }
    }
}
