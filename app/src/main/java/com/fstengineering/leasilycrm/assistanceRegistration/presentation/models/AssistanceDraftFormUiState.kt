package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardDetailsUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

sealed interface AssistanceDraftFormUiState {
    data object Loading : AssistanceDraftFormUiState

    data class Loaded(
        val id: Long? = null,
        val createDateTime: AppDateTime? = null,
        val updateDateTime: AppDateTime? = null,
        val preferredStartDate: AppDate? = null,
        val preferredEndDate: AppDate? = null,
        val hasFlexibilityForPeriod: Boolean = false,
        val maxAcceptedNightlyRate: AppDecimal? = null,
        val adultsCount: Int = 0,
        val kidsCount: Int = 0,
        val babiesCount: Int = 0,
        val animalsCount: Int = 0,
        val bedroomsCount: Int = 0,
        val enSuitesCount: Int = 0,
        val doubleBedsCount: Int = 0,
        val singleBedsCount: Int = 0,
        val bathroomsCount: Int = 0,
        val parkingSpotsCount: Int = 0,
        val preferredNeighborhoods: ImmutableList<String> = persistentListOf(),
        val preferredSeaProximityTypeId: Long? = null,
        val preferredDistanceToSea: AppDecimal? = null,
        val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic> = persistentListOf(),
        val observations: String? = null,
        val sentProperties: ImmutableList<PropertyCardDetailsUiState> = persistentListOf(),
        val interactions: ImmutableList<AssistanceInteractionDraftFormUiState.Loaded> = persistentListOf(),
        val leadTenantId: Long,
        val leadTenantName: String,
        val hasDomainError: Boolean = false,
    ) : AssistanceDraftFormUiState {
        fun toCommand() = AssistanceCommandModel(
            id = id,
            createDateTime = createDateTime,
            updateDateTime = updateDateTime,
            preferredStartDate = preferredStartDate,
            preferredEndDate = preferredEndDate,
            hasFlexibilityForPeriod = hasFlexibilityForPeriod,
            maxAcceptedNightlyRate = maxAcceptedNightlyRate,
            adultsCount = adultsCount,
            kidsCount = kidsCount,
            babiesCount = babiesCount,
            animalsCount = animalsCount,
            bedroomsCount = bedroomsCount,
            enSuitesCount = enSuitesCount,
            doubleBedsCount = doubleBedsCount,
            singleBedsCount = singleBedsCount,
            bathroomsCount = bathroomsCount,
            parkingSpotsCount = parkingSpotsCount,
            preferredNeighborhoods = preferredNeighborhoods,
            preferredSeaProximityTypeId = preferredSeaProximityTypeId,
            preferredDistanceToSea = preferredDistanceToSea,
            preferredPropertyCharacteristics = preferredPropertyCharacteristics,
            observations = observations,
            sentPropertiesIds = sentProperties.map { it.id }.toPersistentList(),
            interactions = interactions
                .map { it.toCommand() }
                .toPersistentList(),
            leadTenantId = leadTenantId,
        )

        companion object {
            fun fromReadModel(readModel: AssistanceReadModel): Loaded {
                if (readModel.assistance == null) {
                    return Loaded(
                        leadTenantId = readModel.leadTenantId,
                        leadTenantName = readModel.leadTenantName,
                    )
                }

                val a = readModel.assistance

                return Loaded(
                    id = a.id,
                    createDateTime = a.createDateTime,
                    updateDateTime = a.updateDateTime,
                    preferredStartDate = a.preferredPeriod?.startDate,
                    preferredEndDate = a.preferredPeriod?.endDate,
                    hasFlexibilityForPeriod = a.hasFlexibilityForPeriod,
                    maxAcceptedNightlyRate = a.maxAcceptedNightlyRate,
                    adultsCount = a.guestsCount.adultsCount,
                    kidsCount = a.guestsCount.kidsCount,
                    babiesCount = a.guestsCount.babiesCount,
                    animalsCount = a.guestsCount.animalsCount,
                    bedroomsCount = a.preferredFeaturesCount.bedroomsCount,
                    enSuitesCount = a.preferredFeaturesCount.enSuitesCount,
                    doubleBedsCount = a.preferredFeaturesCount.doubleBedsCount,
                    singleBedsCount = a.preferredFeaturesCount.singleBedsCount,
                    bathroomsCount = a.preferredFeaturesCount.bathroomsCount,
                    parkingSpotsCount = a.preferredFeaturesCount.parkingSpotsCount,
                    preferredNeighborhoods = a.preferredNeighborhoods.toPersistentList(),
                    preferredSeaProximityTypeId = a.preferredSeaProximityType?.id,
                    preferredDistanceToSea = (a.preferredSeaProximityType as? PropertySeaProximityType.DistanceToSea)?.distance,
                    preferredPropertyCharacteristics = a.preferredPropertyCharacteristics,
                    observations = a.observations,
                    sentProperties = readModel.sentProperties
                        .map { PropertyCardDetailsUiState.fromDomain(it) }
                        .toPersistentList(),
                    interactions = a.interactions
                        .map { AssistanceInteractionDraftFormUiState.Loaded.fromDomain(it) }
                        .toPersistentList(),
                    leadTenantId = a.tenantId,
                    leadTenantName = readModel.leadTenantName,
                )
            }
        }
    }
}
