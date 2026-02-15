package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristicAssistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSentProperty
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
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
    private val activeSearchedNeighborhoods: List<AssistanceSearchedNeighborhood>
        get() = searchedNeighborhoods
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    private val activeCharacteristics: List<AssistanceCharacteristicAssistance>
        get() {
            val activeCharacteristicIds = checkedCharacteristics
                .filter { !it.isDeleted }
                .map { it.assistanceCharacteristicId }
                .toSet()

            return activeCharacteristicIds.map { activeCharacteristicId ->
                val characteristic = allCharacteristics.find { it.id == activeCharacteristicId }

                AssistanceCharacteristicAssistance(
                    assistanceId = requireNotNull(assistance.id),
                    assistanceCharacteristic = requireNotNull(characteristic).toDomain(),
                    createDateTime = assistance.createDateTime,
                    updateDateTime = assistance.updateDateTime,
                    isDeleted = false,
                )
            }
        }

    private val activeSentProperties: List<AssistanceSentProperty>
        get() {
            val activeSentPropertyIds = checkedSentProperties
                .filter { !it.isDeleted }
                .map { it.propertyId }
                .toSet()

            return activeSentPropertyIds.map { activeSentPropertyId ->
                val sentProperty = allSentProperties.find { it.property.id == activeSentPropertyId }

                AssistanceSentProperty(
                    assistanceId = requireNotNull(assistance.id),
                    property = requireNotNull(sentProperty).toDomain(),
                    createDateTime = assistance.createDateTime,
                    updateDateTime = assistance.updateDateTime,
                    isDeleted = false,
                )
            }
        }

    private val activeInteractions: List<AssistanceInteraction>
        get() = interactions
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    fun toDomain() = Assistance(
        id = requireNotNull(assistance.id),
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
        tenantId = requireNotNull(tenant.id),
        searchedNeighborhoods = activeSearchedNeighborhoods.toImmutableList(),
        seaProximityType = seaProximityType?.toDomain(),
        characteristics = activeCharacteristics.toImmutableList(),
        sentProperties = activeSentProperties.toImmutableList(),
        interactions = activeInteractions.toImmutableList(),
    )
}
