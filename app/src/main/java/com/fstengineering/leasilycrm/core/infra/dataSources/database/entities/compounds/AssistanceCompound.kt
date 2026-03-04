package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.Neighborhood
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

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
        entity = AssistancePreferredNeighborhoodEntity::class,
    )
    val preferredNeighborhoods: List<AssistancePreferredNeighborhoodEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistancePropertyCharacteristicCrossRefEntity::class
    )
    val preferredPropertyCharacteristics: List<AssistancePropertyCharacteristicCrossRefEntity>,
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
    private val sentPropertiesIds: ImmutableList<Long>
        get() {
            return checkedSentProperties
                .map { it.propertyId }
                .toSet()
                .mapNotNull { sentPropertyId ->
                    allSentProperties.find { it.property.id == sentPropertyId }?.property?.id
                }
                .toImmutableList()
        }

    fun toDomain(): AppResultCompound<Assistance, DomainError> {
        val errors = persistentListOf<DomainError>()

        val preferredNeighborhoods = preferredNeighborhoods
            .mapNotNull { neighborhood ->
                Neighborhood.parse(neighborhood.neighborhood)
                    .fold(onError = { errors.add(it); null }, onSuccess = { it })
            }
            .toImmutableList()

        val preferredCharacteristicIds = preferredPropertyCharacteristics
            .map { it.propertyCharacteristicId }
            .toSet()
            .mapNotNull { propertyCharacteristicId ->
                PropertyCharacteristic.parse(propertyCharacteristicId)
                    .fold(onError = { errors.add(it); null }, onSuccess = { it })
            }
            .toImmutableList()

        val interactions = interactions
            .mapNotNull { interaction ->
                interaction.toDomain()
                    .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })
            }
            .toPersistentList()

        val parsed = Assistance.parse(
            id = assistance.id,
            createDateTime = assistance.createDateTime,
            updateDateTime = assistance.updateDateTime,
            preferredStartDate = assistance.searchedStartDate,
            preferredEndDate = assistance.searchedEndDate,
            hasFlexibilityForPeriod = assistance.hasFlexibilityForPeriod,
            maxAcceptedNightlyRate = assistance.maxAcceptedNightlyRate,
            adultsCount = assistance.adults,
            kidsCount = assistance.kids,
            babiesCount = assistance.babies,
            animalsCount = assistance.animals,
            bedroomsCount = assistance.bedrooms,
            enSuitesCount = assistance.enSuites,
            doubleBedsCount = assistance.doubleBeds,
            singleBedsCount = assistance.singleBeds,
            bathroomsCount = assistance.bathrooms,
            parkingSpotsCount = assistance.parkingSpots,
            preferredSeaProximityTypeId = assistance.propertySeaProximityTypeId,
            preferredDistanceToSea = assistance.distanceToSea,
            observations = assistance.observations,
            isFinished = assistance.isFinished,
            tenantId = requireNotNull(tenant.id),
            preferredNeighborhoods = preferredNeighborhoods,
            preferredPropertyCharacteristics = preferredCharacteristicIds,
            sentPropertiesIds = sentPropertiesIds,
            interactions = interactions,
            inconsistencies = persistentListOf(),
            contractVersion = assistance.contractVersion,
        )
            .fold(onErrors = { errors.addAll(0, it); null }, onSuccess = { it })

        if (errors.isNotEmpty()) return AppResultCompound.Errors(errors)

        return AppResultCompound.Success(data = requireNotNull(parsed))
    }
}
