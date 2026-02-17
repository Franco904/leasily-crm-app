package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.entities.AssistancePropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceSentProperty
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
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
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistancePropertyCharacteristicCrossRefEntity::class
    )
    val checkedCharacteristics: List<AssistancePropertyCharacteristicCrossRefEntity>,
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

    private val activeCharacteristics: List<AssistancePropertyCharacteristic>
        get() {
            val activeCharacteristicIds = checkedCharacteristics
                .filter { !it.isDeleted }
                .map { it.propertyCharacteristicId }
                .toSet()

            return activeCharacteristicIds.map { activeCharacteristicId ->
                val characteristic = PropertyCharacteristic.parse(activeCharacteristicId)

                AssistancePropertyCharacteristic(
                    propertyCharacteristic = characteristic,
                    createDateTime = assistance.createDateTime,
                    updateDateTime = assistance.updateDateTime,
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
                    property = requireNotNull(sentProperty).toDomain(),
                    createDateTime = assistance.createDateTime,
                    updateDateTime = assistance.updateDateTime,
                )
            }
        }

    private val activeInteractions: List<AssistanceInteraction>
        get() = interactions
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    fun toDomain() = Assistance.parse(
        id = assistance.id,
        createDateTime = assistance.createDateTime,
        updateDateTime = assistance.updateDateTime,
        searchedStartDate = assistance.searchedStartDate,
        searchedEndDate = assistance.searchedEndDate,
        hasFlexibilityForPeriod = assistance.hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = assistance.maxAcceptedNightlyRate,
        adults = assistance.adults,
        kids = assistance.kids,
        babies = assistance.babies,
        animals = assistance.animals,
        bedrooms = assistance.bedrooms,
        enSuites = assistance.enSuites,
        doubleBeds = assistance.doubleBeds,
        singleBeds = assistance.singleBeds,
        bathrooms = assistance.bathrooms,
        parkingSpots = assistance.parkingSpots,
        seaProximityTypeId = assistance.propertySeaProximityTypeId,
        distanceToSea = assistance.distanceToSea,
        observations = assistance.observations,
        isFinished = assistance.isFinished,
        isDeleted = assistance.isDeleted,
        tenantId = requireNotNull(tenant.id),
        searchedNeighborhoods = activeSearchedNeighborhoods.toImmutableList(),
        characteristics = activeCharacteristics.toImmutableList(),
        sentProperties = activeSentProperties.toImmutableList(),
        interactions = activeInteractions.toImmutableList(),
        inconsistencies = assistance.inconsistencies,
        contractVersion = assistance.contractVersion,
    )
}
