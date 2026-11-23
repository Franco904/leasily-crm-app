package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.mappers

import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoSelectableImovelReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.ImovelEnviadoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.InteracaoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.InteracaoDraftFormUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.toPersistentList

fun AtendimentoReadModel.toAtendimentoDraftFormUiState() = AtendimentoDraftFormUiState.Loaded(
    id = id,
    clienteId = clienteId,
    clienteName = clienteName,
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
    preferredImovelCharacteristics = preferredImovelCharacteristics,
    observations = observations,
    imoveisEnviados = imoveisEnviados.map { it.toImovelCardUiState() }.toPersistentList(),
    interacoes = interacoes.map { it.toInteracaoDraftFormUiState() }.toPersistentList(),
)

fun ImovelEnviadoReadModel.toImovelCardUiState(): ImovelCardUiState {
    val numberText = if (number != null) ", $number" else ""

    return ImovelCardUiState(
        id = id,
        mainPictureUriString = mainPictureUriString,
        neighborhood = neighborhood,
        streetAddressAndNumber = "${streetAddress}$numberText",
        type = type,
        seaProximityTypeId = seaProximityTypeId,
        distanceToSea = distanceToSea,
        bedroomsCount = bedroomsCount,
    )
}

fun AtendimentoSelectableImovelReadModel.toImovelCardUiState(): ImovelCardUiState {
    val numberText = if (number != null) ", $number" else ""

    return ImovelCardUiState(
        id = id,
        mainPictureUriString = mainPictureUriString,
        neighborhood = neighborhood,
        streetAddressAndNumber = "${streetAddress}$numberText",
        type = type,
        seaProximityTypeId = seaProximityTypeId,
        distanceToSea = distanceToSea,
        bedroomsCount = bedroomsCount,
    )
}

private fun InteracaoReadModel.toInteracaoDraftFormUiState() = InteracaoDraftFormUiState.Loaded(
    id = id,
    date = date,
    title = title,
    description = description,
)
