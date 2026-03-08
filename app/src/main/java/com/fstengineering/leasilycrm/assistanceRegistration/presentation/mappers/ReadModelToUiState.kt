package com.fstengineering.leasilycrm.assistanceRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceSelectablePropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceInteractionDraftFormUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardDetailsUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun AssistanceReadModel.toDraftForm() = AssistanceDraftFormUiState.Loaded(
    id = id,
    leadTenantId = leadTenantId,
    leadTenantName = leadTenantName,
    createDateTime = createDateTime,
    updateDateTime = updateDateTime,
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
    preferredPropertyCharacteristics = preferredPropertyCharacteristics ?: persistentListOf(),
    observations = observations,
    sentProperties = sentProperties
        ?.map { it.toCardDetails() }
        ?.toPersistentList() ?: persistentListOf(),
    interactions = interactions
        ?.map { AssistanceInteractionDraftFormUiState.Loaded.fromReadModel(it) }
        ?.toPersistentList() ?: persistentListOf(),
)

fun AssistanceSentPropertyReadModel.toCardDetails(): PropertyCardDetailsUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return PropertyCardDetailsUiState(
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

fun AssistanceSelectablePropertyReadModel.toCardDetails(): PropertyCardDetailsUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return PropertyCardDetailsUiState(
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
