package com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import kotlinx.collections.immutable.ImmutableList

data class AssistanceReadModel(
    val id: Long,
    val updateDateTime: AppDateTime,
    val preferredStartDate: AppDate?,
    val preferredEndDate: AppDate?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val adultsCount: Int?,
    val kidsCount: Int?,
    val babiesCount: Int?,
    val animalsCount: Int?,
    val bedroomsCount: Int?,
    val enSuitesCount: Int?,
    val doubleBedsCount: Int?,
    val singleBedsCount: Int?,
    val bathroomsCount: Int?,
    val parkingSpotsCount: Int?,
    val preferredSeaProximityTypeId: Long?,
    val preferredDistanceToSea: AppDecimal?,
    val observations: String?,
    val isFinished: Boolean,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    val interactions: ImmutableList<AssistanceInteractionReadModel>,
    val sentProperties: ImmutableList<AssistanceSentPropertyReadModel>,
) {
    companion object {
        fun create(
            assistanceRaw: AssistanceRawReadModel,
            sentProperties: ImmutableList<AssistanceSentPropertyReadModel>,
        ) = AssistanceReadModel(
            id = requireNotNull(assistanceRaw.id),
            updateDateTime = assistanceRaw.updateDateTime,
            preferredStartDate = assistanceRaw.preferredStartDate,
            preferredEndDate = assistanceRaw.preferredEndDate,
            hasFlexibilityForPeriod = assistanceRaw.hasFlexibilityForPeriod,
            maxAcceptedNightlyRate = assistanceRaw.maxAcceptedNightlyRate,
            adultsCount = assistanceRaw.adultsCount,
            kidsCount = assistanceRaw.kidsCount,
            babiesCount = assistanceRaw.babiesCount,
            animalsCount = assistanceRaw.animalsCount,
            bedroomsCount = assistanceRaw.bedroomsCount,
            enSuitesCount = assistanceRaw.enSuitesCount,
            doubleBedsCount = assistanceRaw.doubleBedsCount,
            singleBedsCount = assistanceRaw.singleBedsCount,
            bathroomsCount = assistanceRaw.bathroomsCount,
            parkingSpotsCount = assistanceRaw.parkingSpotsCount,
            preferredSeaProximityTypeId = assistanceRaw.preferredSeaProximityTypeId,
            preferredDistanceToSea = assistanceRaw.preferredDistanceToSea,
            preferredNeighborhoods = assistanceRaw.preferredNeighborhoods,
            preferredPropertyCharacteristics = assistanceRaw.preferredPropertyCharacteristics,
            observations = assistanceRaw.observations,
            sentProperties = sentProperties,
            interactions = assistanceRaw.interactions,
            isFinished = assistanceRaw.isFinished,
        )
    }
}
