package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal

@Entity(
    tableName = "assistances",
    foreignKeys = [
        ForeignKey(
            entity = LeadTenantEntity::class,
            parentColumns = ["id"],
            childColumns = ["lead_tenant_id"],
        ),
    ],
    indices = [
        Index("lead_tenant_id"),
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
    @ColumnInfo(name = "lead_tenant_id") val leadTenantId: Long,
    @ColumnInfo(name = "contract_version") val contractVersion: Int,
)
