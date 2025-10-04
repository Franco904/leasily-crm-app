package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristicAssistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSentProperty
import kotlinx.collections.immutable.toImmutableList

data class AssistanceCompound(
    @Embedded val assistance: AssistanceEntity,
    @Relation(
        parentColumn = "tenant_id",
        entityColumn = "id",
        entity = TenantEntity::class,
    )
    val tenant: TenantEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceSearchedNeighborhoodEntity::class,
    )
    val searchedNeighborhoods: List<AssistanceSearchedNeighborhoodEntity>,
    @Relation(
        parentColumn = "property_sea_proximity_type_id",
        entityColumn = "id",
        entity = PropertySeaProximityTypeEntity::class,
    )
    val seaProximityType: PropertySeaProximityTypeEntity?,
    @Relation(
        parentColumn = "id",
        entity = AssistanceCharacteristicEntity::class,
        entityColumn = "id",
        associateBy = Junction(
            value = AssistanceCharacteristicAssistanceCrossRefEntity::class,
            parentColumn = "assistance_id",
            entityColumn = "assistance_characteristic_id"
        )
    )
    val allCharacteristics: List<AssistanceCharacteristicEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceCharacteristicAssistanceCrossRefEntity::class
    )
    val checkedCharacteristics: List<AssistanceCharacteristicAssistanceCrossRefEntity>,
    @Relation(
        parentColumn = "id",
        entity = PropertyEntity::class,
        entityColumn = "id",
        associateBy = Junction(
            value = AssistanceSentPropertyCrossRefEntity::class,
            parentColumn = "assistance_id",
            entityColumn = "property_id"
        )
    )
    val allSentProperties: List<PropertyCompound>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceSentPropertyCrossRefEntity::class
    )
    val checkedSentProperties: List<AssistanceSentPropertyCrossRefEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceInteractionEntity::class,
    )
    val interactions: List<AssistanceInteractionEntity>,
) {
    val activeSearchedNeighborhoods: List<AssistanceSearchedNeighborhoodEntity>
        get() = searchedNeighborhoods.filter { !it.isDeleted }

    val activeCharacteristics: List<AssistanceCharacteristicAssistance>
        get() {
            val activeCharacteristicIds = checkedCharacteristics
                .filter { !it.isDeleted }
                .map { it.assistanceCharacteristicId }
                .toSet()

            return activeCharacteristicIds.map { activeCharacteristicId ->
                val characteristic = allCharacteristics.find { it.id == activeCharacteristicId }

                AssistanceCharacteristicAssistance(
                    assistanceId = assistance.id,
                    assistanceCharacteristic = characteristic?.toDomain(),
                    isDeleted = false,
                )
            }
        }

    val activeSentProperties: List<AssistanceSentProperty>
        get() {
            val activeSentPropertyIds = checkedSentProperties
                .filter { !it.isDeleted }
                .map { it.propertyId }
                .toSet()

            return activeSentPropertyIds.map { activeSentPropertyId ->
                val sentProperty = allSentProperties.find { it.property.id == activeSentPropertyId }

                AssistanceSentProperty(
                    assistanceId = assistance.id,
                    property = sentProperty?.toDomain(),
                    isDeleted = false,
                )
            }
        }

    val activeInteractions: List<AssistanceInteractionEntity>
        get() = interactions.filter { !it.isDeleted }

    fun toDomain() = Assistance(
        id = assistance.id,
        createDateTime = assistance.createDateTime,
        updateDateTime = assistance.updateDateTime,
        searchedStartDate = assistance.searchedStartDate,
        searchedEndDate = assistance.searchedEndDate,
        hasFlexibilityForPeriod = assistance.hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = assistance.maxAcceptedNightlyRate,
        qtAdultGuests = assistance.qtAdultGuests,
        qtKidGuests = assistance.qtKidGuests,
        qtBabyGuests = assistance.qtBabyGuests,
        qtAnimalGuests = assistance.qtAnimalGuests,
        qtBedrooms = assistance.qtBedrooms,
        qtEnSuites = assistance.qtEnSuites,
        qtDoubleBeds = assistance.qtDoubleBeds,
        qtSingleBeds = assistance.qtSingleBeds,
        qtBathrooms = assistance.qtBathrooms,
        qtParkingSpots = assistance.qtParkingSpots,
        distanceToSea = assistance.distanceToSea,
        observations = assistance.observations,
        isFinished = assistance.isFinished,
        isDeleted = assistance.isDeleted,
        tenant = tenant.toDomain(),
        searchedNeighborhoods = activeSearchedNeighborhoods.map { it.toDomain() }.toImmutableList(),
        seaProximityType = seaProximityType?.toDomain(),
        characteristics = activeCharacteristics.toImmutableList(),
        sentProperties = activeSentProperties.toImmutableList(),
        interactions = activeInteractions.map { it.toDomain() }.toImmutableList(),
    )
}
