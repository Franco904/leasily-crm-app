package com.fstengineering.leasilycrm.assistanceRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSelectableImovelReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceInteractionDraftFormUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun AssistanceReadModel.toUiState() = AssistanceDraftFormUiState.Loaded(
    id = id,
    leadTenantId = leadTenantId,
    leadTenantName = leadTenantName,
    createDateTime = createDateTime,
    preferredStartDate = preferredStartDate,
    preferredEndDate = preferredEndDate,
    hasFlexibilityForPeriod = hasFlexibilityForPeriod ?: false,
    maxAcceptedNightlyRate = maxAcceptedNightlyRate,
    adultsCount = adultsCount ?: 0,
    kidsCount = kidsCount ?: 0,
    babiesCount = babiesCount ?: 0,
    animalsCount = animalsCount ?: 0,
    bedroomsCount = bedroomsCount ?: 0,
    enSuitesCount = enSuitesCount ?: 0,
    doubleBedsCount = doubleBedsCount ?: 0,
    singleBedsCount = singleBedsCount ?: 0,
    bathroomsCount = bathroomsCount ?: 0,
    parkingSpotsCount = parkingSpotsCount ?: 0,
    preferredNeighborhoods = preferredNeighborhoods?.toPersistentList() ?: persistentListOf(),
    preferredSeaProximityTypeId = preferredSeaProximityTypeId,
    preferredDistanceToSea = preferredDistanceToSea,
    preferredImovelCharacteristics = preferredImovelCharacteristics ?: persistentListOf(),
    observations = observations,
    sentImoveis = sentImoveis
        ?.map { it.toUiState() }
        ?.toPersistentList() ?: persistentListOf(),
    interactions = interactions
        ?.map { it.toUiState() }
        ?.toPersistentList() ?: persistentListOf(),
)

fun AssistanceSentImovelReadModel.toUiState(): ImovelCardUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return ImovelCardUiState(
        id = requireNotNull(id),
        mainPictureUri = mainPictureUriString?.toUri(),
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
        seaProximityTypeId = seaProximityTypeId,
        distanceToSea = distanceToSea,
        bedroomsCount = bedroomsCount,
    )
}

fun AssistanceSelectableImovelReadModel.toUiState(): ImovelCardUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return ImovelCardUiState(
        id = requireNotNull(id),
        mainPictureUri = mainPictureUriString?.toUri(),
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
        seaProximityTypeId = seaProximityTypeId,
        distanceToSea = distanceToSea,
        bedroomsCount = bedroomsCount,
    )
}

private fun AssistanceInteractionReadModel.toUiState() =
    AssistanceInteractionDraftFormUiState.Loaded(
        id = id,
        date = date,
        title = title,
        description = description,
    )
