package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import kotlinx.collections.immutable.ImmutableList

data class AssistanceReadModel(
    val id: Long?,
    val leadTenantId: Long,
    val leadTenantName: String,
    val createDateTime: AppDateTime?,
    val preferredStartDate: AppDate?,
    val preferredEndDate: AppDate?,
    val hasFlexibilityForPeriod: Boolean?,
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
    val preferredNeighborhoods: ImmutableList<String>?,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>?,
    val observations: String?,
    val sentProperties: ImmutableList<AssistanceSentPropertyReadModel>?,
    val interactions: ImmutableList<AssistanceInteractionReadModel>?,
    val isFinished: Boolean?,
) : QueryUseCase.ReadModel {
    companion object {
        fun create(
            leadTenantId: Long,
            leadTenantName: String,
            assistanceRaw: AssistanceRawReadModel?,
            sentProperties: ImmutableList<AssistanceSentPropertyReadModel>,
        ) = AssistanceReadModel(
            id = assistanceRaw?.id,
            leadTenantId = leadTenantId,
            leadTenantName = leadTenantName,
            createDateTime = assistanceRaw?.createDateTime,
            preferredStartDate = assistanceRaw?.preferredStartDate,
            preferredEndDate = assistanceRaw?.preferredEndDate,
            hasFlexibilityForPeriod = assistanceRaw?.hasFlexibilityForPeriod,
            maxAcceptedNightlyRate = assistanceRaw?.maxAcceptedNightlyRate,
            adultsCount = assistanceRaw?.adultsCount,
            kidsCount = assistanceRaw?.kidsCount,
            babiesCount = assistanceRaw?.babiesCount,
            animalsCount = assistanceRaw?.animalsCount,
            bedroomsCount = assistanceRaw?.bedroomsCount,
            enSuitesCount = assistanceRaw?.enSuitesCount,
            doubleBedsCount = assistanceRaw?.doubleBedsCount,
            singleBedsCount = assistanceRaw?.singleBedsCount,
            bathroomsCount = assistanceRaw?.bathroomsCount,
            parkingSpotsCount = assistanceRaw?.parkingSpotsCount,
            preferredSeaProximityTypeId = assistanceRaw?.preferredSeaProximityTypeId,
            preferredDistanceToSea = assistanceRaw?.preferredDistanceToSea,
            preferredNeighborhoods = assistanceRaw?.preferredNeighborhoods,
            preferredPropertyCharacteristics = assistanceRaw?.preferredPropertyCharacteristics,
            observations = assistanceRaw?.observations,
            sentProperties = sentProperties,
            interactions = assistanceRaw?.interactions,
            isFinished = assistanceRaw?.isFinished,
        )
    }
}
