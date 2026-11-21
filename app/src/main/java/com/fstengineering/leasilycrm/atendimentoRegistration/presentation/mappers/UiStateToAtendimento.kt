package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.mappers

import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Atendimento
import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Interacao
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import kotlinx.collections.immutable.toPersistentList

fun AtendimentoDraftFormUiState.Loaded.toAtendimento(): AppResult<Atendimento> {
    val errors = mutableListOf<DomainError>()

    val parsedInteracoes = interacoes
        .mapNotNull { interacao ->
            Interacao.parse(
                id = interacao.id,
                date = interacao.date,
                title = interacao.title,
                description = interacao.description,
            )
                .fold(onError = { errors.addAll(it.toDomainErrors()); null }, onSuccess = { it })
        }
        .toPersistentList()

    val parsed = Atendimento.parse(
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
        preferredImovelCharacteristics = preferredImovelCharacteristics,
        observations = observations,
        imoveisEnviadosIds = imoveisEnviados.map { it.id }.toPersistentList(),
        interacoes = parsedInteracoes,
        clienteId = clienteId,
        isFinished = false,
    )
        .fold(onError = { errors.addAll(0, it.toDomainErrors()); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

    return AppResult.Success(data = requireNotNull(parsed))
}
