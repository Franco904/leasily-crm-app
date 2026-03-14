package com.fstengineering.leasilycrm.assistanceRegistration.presentation.mappers

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun AssistanceDraftFormUiState.Loaded.toCommandModel(): AssistanceCommandModel {
    return AssistanceCommandModel(
        id = id,
        createDateTime = createDateTime,
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
}
