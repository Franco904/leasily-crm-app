package com.fstengineering.leasilycrm.clienteDetails.presentation.mappers

import com.fstengineering.leasilycrm.clienteDetails.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.ImovelEnviadoReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.InteracaoReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.StayReadModel
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.AtendimentoUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteDetailsUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteHistoryItemUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.InteracaoUiState
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun ClienteReadModel.toClienteDetailsUiState(): ClienteDetailsUiState.Loaded {
    val currentStayId = stays.find { !it.isCheckOutDone }?.id

    val allAtendimentosUiState = atendimentos.map { it.toUiState() }.toPersistentList()
    val currentAtendimento = allAtendimentosUiState.find { !it.isFinished }
    val pastAtendimentos = allAtendimentosUiState.filter { it.isFinished }.toPersistentList()

    val historyByYear = buildHistory(
        atendimentos = pastAtendimentos,
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
        currentAtendimento = currentAtendimento,
        pastAtendimentos = pastAtendimentos,
        historyByYear = historyByYear,
    )
}

private fun buildHistory(
    atendimentos: ImmutableList<AtendimentoUiState>,
    stays: List<StayReadModel>,
): ImmutableMap<Int, ImmutableList<ClienteHistoryItemUiState>> {
    val atendimentoItems = atendimentos.map {
        ClienteHistoryItemUiState(
            id = it.id,
            isAtendimento = true,
            dateTime = it.updateDateTime,
        )
    }

    val stayItems = stays
        .filter { it.isCheckOutDone }
        .map {
            ClienteHistoryItemUiState(
                id = it.id,
                isAtendimento = false,
                dateTime = it.checkInDateTime,
            )
        }

    return (atendimentoItems + stayItems)
        .sortedByDescending { it.dateTime }
        .groupBy { it.dateTime.year }
        .mapValues { (_, items) -> items.toPersistentList() }
        .toPersistentMap()
}

private fun AtendimentoReadModel.toUiState(): AtendimentoUiState {
    val sortedInteracoes = interacoes
        .map { it.toUiState() }
        .sortedByDescending { it.date }
        .groupBy { interacao ->
            AppYearMonth.of(
                year = interacao.date.year,
                month = interacao.date.month,
            )
        }
        .mapValues { (_, interacoes) -> interacoes.toPersistentList() }
        .toPersistentMap()

    val sortedSentImoveis = sentImoveis
        .map { it.toUiState() }
        .toPersistentList()

    return AtendimentoUiState(
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
        lastInteracao = sortedInteracoes.values.flatten().firstOrNull(),
        interacoesByMonth = sortedInteracoes,
        isFinished = isFinished,
    )
}

private fun InteracaoReadModel.toUiState() = InteracaoUiState(
    id = id,
    date = date,
    title = title,
    description = description,
)

private fun ImovelEnviadoReadModel.toUiState(): ImovelCardUiState {
    val streetAddressAndNumber = buildString {
        append(streetAddress)
        if (number != null) append(", $number")
    }

    return ImovelCardUiState(
        id = id,
        mainPictureUriString = mainPictureUriString,
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
    )
}
