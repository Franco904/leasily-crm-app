package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AssistanceGuestsCount
import com.fstengineering.leasilycrm.core.domain.valueObjects.AssistancePreferredFeaturesCount
import com.fstengineering.leasilycrm.core.domain.valueObjects.AssistancePreferredPeriod
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class Assistance private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val preferredPeriod: AssistancePreferredPeriod?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val guestsCount: AssistanceGuestsCount,
    val preferredFeaturesCount: AssistancePreferredFeaturesCount,
    val preferredSeaProximityType: PropertySeaProximityType?,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    val observations: String?,
    val sentPropertiesIds: ImmutableList<Long>,
    val interactions: ImmutableList<AssistanceInteraction>,
    val tenantId: Long,
    val isFinished: Boolean,
    val inconsistencies: ImmutableList<DomainError>,
    val contractVersion: Int,
) {
    fun finish() = copyWith(isFinished = true)

    fun unfinish() = copyWith(isFinished = false)

    private fun copyWith(
        id: Long? = this.id,
        createDateTime: AppDateTime? = this.createDateTime,
        updateDateTime: AppDateTime? = this.updateDateTime,
        searchedPeriod: AssistancePreferredPeriod? = this.preferredPeriod,
        hasFlexibilityForPeriod: Boolean = this.hasFlexibilityForPeriod,
        maxAcceptedNightlyRate: AppDecimal? = this.maxAcceptedNightlyRate,
        guestsCount: AssistanceGuestsCount = this.guestsCount,
        preferredFeaturesCount: AssistancePreferredFeaturesCount = this.preferredFeaturesCount,
        preferredSeaProximityType: PropertySeaProximityType? = this.preferredSeaProximityType,
        preferredNeighborhoods: ImmutableList<String> = this.preferredNeighborhoods,
        preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic> = this.preferredPropertyCharacteristics,
        observations: String? = this.observations,
        sentPropertiesIds: ImmutableList<Long> = this.sentPropertiesIds,
        interactions: ImmutableList<AssistanceInteraction> = this.interactions,
        tenantId: Long = this.tenantId,
        isFinished: Boolean = this.isFinished,
        inconsistencies: ImmutableList<DomainError> = this.inconsistencies,
        contractVersion: Int = this.contractVersion,
    ) = Assistance(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        preferredPeriod = searchedPeriod,
        hasFlexibilityForPeriod = hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = maxAcceptedNightlyRate,
        guestsCount = guestsCount,
        preferredFeaturesCount = preferredFeaturesCount,
        preferredSeaProximityType = preferredSeaProximityType,
        preferredNeighborhoods = preferredNeighborhoods,
        preferredPropertyCharacteristics = preferredPropertyCharacteristics,
        observations = observations,
        sentPropertiesIds = sentPropertiesIds,
        interactions = interactions,
        tenantId = tenantId,
        isFinished = isFinished,
        inconsistencies = inconsistencies,
        contractVersion = contractVersion,
    )

    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            preferredStartDate: AppDate?,
            preferredEndDate: AppDate?,
            hasFlexibilityForPeriod: Boolean,
            maxAcceptedNightlyRate: AppDecimal?,
            adultsCount: Int,
            kidsCount: Int,
            babiesCount: Int,
            animalsCount: Int,
            bedroomsCount: Int,
            enSuitesCount: Int,
            doubleBedsCount: Int,
            singleBedsCount: Int,
            bathroomsCount: Int,
            parkingSpotsCount: Int,
            preferredSeaProximityTypeId: Long?,
            preferredDistanceToSea: AppDecimal?,
            preferredNeighborhoods: ImmutableList<String>,
            preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
            observations: String?,
            sentPropertiesIds: ImmutableList<Long>,
            interactions: ImmutableList<AssistanceInteraction>,
            tenantId: Long?,
            isFinished: Boolean,
            inconsistencies: ImmutableList<DomainError>,
            contractVersion: Int,
        ): AppResultCompound<Assistance, DomainError> {
            val errors = mutableListOf<DomainError>()

            val tenantId = tenantId ?: run {
                errors.add(DomainError.AssistanceOtherFields.TenantRequired); null
            }

            val preferredPeriod =
                if (preferredStartDate == null && preferredEndDate == null) null
                else AssistancePreferredPeriod.parse(
                    startDate = preferredStartDate,
                    endDate = preferredEndDate,
                )
                    .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val guestsCount = AssistanceGuestsCount.parse(
                adultsCount = adultsCount,
                kidsCount = kidsCount,
                babiesCount = babiesCount,
                animalsCount = animalsCount,
            )
                .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val preferredFeaturesCount = AssistancePreferredFeaturesCount.parse(
                bedroomsCount = bedroomsCount,
                enSuitesCount = enSuitesCount,
                doubleBedsCount = doubleBedsCount,
                singleBedsCount = singleBedsCount,
                bathroomsCount = bathroomsCount,
                parkingSpotsCount = parkingSpotsCount,
            )
                .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val preferredSeaProximityType = preferredSeaProximityTypeId?.let { typeId ->
                PropertySeaProximityType.parse(typeId, distanceToSea = preferredDistanceToSea)
                    .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })
            }

            val isAnyNeighborhoodEmpty = preferredNeighborhoods.any { it.isBlank() }
            if (isAnyNeighborhoodEmpty) errors.add(DomainError.Neighborhood.Required)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = Assistance(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                preferredPeriod = preferredPeriod,
                hasFlexibilityForPeriod = hasFlexibilityForPeriod,
                maxAcceptedNightlyRate = maxAcceptedNightlyRate,
                guestsCount = requireNotNull(guestsCount),
                preferredFeaturesCount = requireNotNull(preferredFeaturesCount),
                preferredSeaProximityType = preferredSeaProximityType,
                isFinished = isFinished,
                preferredNeighborhoods = preferredNeighborhoods,
                preferredPropertyCharacteristics = preferredPropertyCharacteristics,
                observations = observations,
                sentPropertiesIds = sentPropertiesIds,
                interactions = interactions,
                tenantId = requireNotNull(tenantId),
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
