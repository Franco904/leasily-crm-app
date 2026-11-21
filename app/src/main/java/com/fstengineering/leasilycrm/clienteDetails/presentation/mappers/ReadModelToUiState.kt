package com.fstengineering.leasilycrm.clienteDetails.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.StayReadModel
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.AssistanceInteractionUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.AssistanceUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteDetailsUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteHistoryItemUiState
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun ClienteReadModel.toClienteDetailsUiState(): ClienteDetailsUiState.Loaded {
    val currentStayId = stays.find { !it.isCheckOutDone }?.id

    val allAssistancesUiState = assistances.map { it.toUiState() }.toPersistentList()
    val currentAssistance = allAssistancesUiState.find { !it.isFinished }
    val pastAssistances = allAssistancesUiState.filter { it.isFinished }.toPersistentList()

    val historyByYear = buildHistory(
        assistances = pastAssistances,
        stays = stays,
    )

    return ClienteDetailsUiState.Loaded(
        id = id,
        tag = tag,
        name = name,
        city = city,
        identityDocument = identityDocument,
        phone = phone,
        email = email,
        contactOrigin = contactOrigin,
        observations = observations,
        isLead = isLead,
        isOld = isOld,
        currentStayId = currentStayId,
        currentAssistance = currentAssistance,
        pastAssistances = pastAssistances,
        historyByYear = historyByYear,
    )
}

private fun buildHistory(
    assistances: ImmutableList<AssistanceUiState>,
    stays: List<StayReadModel>,
): ImmutableMap<Int, ImmutableList<ClienteHistoryItemUiState>> {
    val assistanceItems = assistances.map {
        ClienteHistoryItemUiState(
            id = it.id,
            isAssistance = true,
            dateTime = it.updateDateTime,
        )
    }

    val stayItems = stays
        .filter { it.isCheckOutDone }
        .map {
            ClienteHistoryItemUiState(
                id = it.id,
                isAssistance = false,
                dateTime = it.checkInDateTime,
            )
        }

    return (assistanceItems + stayItems)
        .sortedByDescending { it.dateTime }
        .groupBy { it.dateTime.year }
        .mapValues { (_, items) -> items.toPersistentList() }
        .toPersistentMap()
}

private fun AssistanceReadModel.toUiState(): AssistanceUiState {
    val sortedInteractions = interactions
        .map { it.toUiState() }
        .sortedByDescending { it.date }
        .groupBy { interaction ->
            AppYearMonth.of(
                year = interaction.date.year,
                month = interaction.date.month,
            )
        }
        .toPersistentMap()

    val sortedSentImoveis = sentImoveis
        .map { it.toUiState() }
        .toPersistentList()

    return AssistanceUiState(
        id = id,
        updateDateTime = updateDateTime,
        searchingStartDate = preferredStartDate,
        searchingEndDate = preferredEndDate,
        hasFlexibilityForPeriod = hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = maxAcceptedNightlyRate,
        qtAdultHospedes = adultsCount ?: 0,
        qtKidHospedes = kidsCount ?: 0,
        qtBabyHospedes = babiesCount ?: 0,
        qtAnimalHospedes = animalsCount ?: 0,
        qtBedrooms = bedroomsCount ?: 0,
        qtEnSuites = enSuitesCount ?: 0,
        qtDoubleBeds = doubleBedsCount ?: 0,
        qtSingleBeds = singleBedsCount ?: 0,
        qtBathrooms = bathroomsCount ?: 0,
        qtParkingSpots = parkingSpotsCount ?: 0,
        preferredSeaProximityTypeId = preferredSeaProximityTypeId,
        distanceToSea = preferredDistanceToSea,
        preferredNeighborhoods = preferredNeighborhoods,
        preferredImovelCharacteristics = preferredImovelCharacteristics,
        observations = observations,
        lastSentImovel = sortedSentImoveis.firstOrNull(),
        sentImoveis = sortedSentImoveis,
        lastInteraction = sortedInteractions.values.flatten().firstOrNull(),
        interactionsByMonth = sortedInteractions,
        isFinished = isFinished,
    )
}

private fun AssistanceInteractionReadModel.toUiState() = AssistanceInteractionUiState(
    id = id,
    date = date,
    title = title,
    description = description,
)

private fun AssistanceSentImovelReadModel.toUiState(): ImovelCardUiState {
    val streetAddressAndNumber = buildString {
        append(streetAddress)
        if (number != null) append(", $number")
    }

    return ImovelCardUiState(
        id = id,
        mainPictureUri = mainPictureUriString?.toUri(),
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
    )
}
