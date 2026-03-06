package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import kotlinx.collections.immutable.ImmutableList

data class AssistanceReadModel(
    val id: Long? = null,
    val leadTenantId: Long,
    val leadTenantName: String,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val preferredStartDate: AppDate? = null,
    val preferredEndDate: AppDate? = null,
    val hasFlexibilityForPeriod: Boolean? = null,
    val maxAcceptedNightlyRate: AppDecimal? = null,
    val adultsCount: Int? = null,
    val kidsCount: Int? = null,
    val babiesCount: Int? = null,
    val animalsCount: Int? = null,
    val bedroomsCount: Int? = null,
    val enSuitesCount: Int? = null,
    val doubleBedsCount: Int? = null,
    val singleBedsCount: Int? = null,
    val bathroomsCount: Int? = null,
    val parkingSpotsCount: Int? = null,
    val preferredSeaProximityTypeId: Long? = null,
    val preferredDistanceToSea: AppDecimal? = null,
    val preferredNeighborhoods: ImmutableList<String>? = null,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>? = null,
    val observations: String? = null,
    val sentProperties: ImmutableList<AssistanceSentPropertyReadModel>? = null,
    val interactions: ImmutableList<AssistanceInteractionReadModel>? = null,
    val isFinished: Boolean? = null,
    val inconsistencies: ImmutableList<DomainError>? = null,
    val contractVersion: Int? = null,
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
            updateDateTime = assistanceRaw?.updateDateTime,
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
            inconsistencies = assistanceRaw?.inconsistencies,
            contractVersion = assistanceRaw?.contractVersion,
        )
    }
}
