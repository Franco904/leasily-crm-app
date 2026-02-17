package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType

@Entity(
    tableName = "properties",
    foreignKeys = [
        ForeignKey(
            entity = PropertyOwnerEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_owner_id"],
        ),
    ],
    indices = [
        Index("property_owner_id"),
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
    @ColumnInfo(name = "bedrooms") val bedrooms: Int,
    @ColumnInfo(name = "en_suites") val enSuites: Int,
    @ColumnInfo(name = "double_beds") val doubleBeds: Int,
    @ColumnInfo(name = "single_beds") val singleBeds: Int,
    @ColumnInfo(name = "bathrooms") val bathrooms: Int,
    @ColumnInfo(name = "parking_spots") val parkingSpots: Int,
    @ColumnInfo(name = "air_conditioners") val airConditioners: Int,
    @ColumnInfo(name = "max_guests") val maxGuests: Int,
    @ColumnInfo(name = "distance_to_sea") val distanceToSea: AppDecimal?,
    @ColumnInfo(name = "included_items") val includedItems: String?,
    @ColumnInfo(name = "has_included_items") val hasIncludedItems: Boolean,
    @ColumnInfo(name = "cleaning_service_tax") val cleaningServiceTax: AppDecimal?,
    @ColumnInfo(name = "has_cleaning_service_included") val hasCleaningServiceIncluded: Boolean,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "property_owner_id") val ownerId: Long,
    @ColumnInfo(name = "property_type_id") val typeId: Long,
    @ColumnInfo(name = "property_sea_proximity_type_id") val seaProximityTypeId: Long?,
    val inconsistencies: String?,
    @ColumnInfo(name = "contract_version") val contractVersion: Int,
) {
    companion object {
        fun fromDomain(domain: Property): PropertyEntity {
            val seaProximityDistanceToSeaType =
                domain.seaProximityType as? PropertySeaProximityType.DistanceToSea

            return PropertyEntity(
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
                bedrooms = domain.bedrooms,
                enSuites = domain.enSuites,
                doubleBeds = domain.doubleBeds,
                singleBeds = domain.singleBeds,
                bathrooms = domain.bathrooms,
                parkingSpots = domain.parkingSpots,
                airConditioners = domain.airConditioners,
                maxGuests = domain.maxGuests,
                seaProximityTypeId = domain.seaProximityType?.id,
                distanceToSea = seaProximityDistanceToSeaType?.distance,
                includedItems = domain.includedItems,
                hasIncludedItems = domain.hasIncludedItems,
                cleaningServiceTax = domain.cleaningServiceTax,
                hasCleaningServiceIncluded = domain.hasCleaningServiceIncluded,
                observations = domain.observations,
                isDeleted = false,
                ownerId = requireNotNull(domain.owner.id),
                typeId = domain.type.id,
                inconsistencies = domain.inconsistencies,
                contractVersion = BuildConfig.CONTRACT_VERSION,
            )
        }
    }
}
