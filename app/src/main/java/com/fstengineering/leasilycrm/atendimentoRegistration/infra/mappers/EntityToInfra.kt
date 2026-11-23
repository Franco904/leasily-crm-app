package com.fstengineering.leasilycrm.atendimentoRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Atendimento
import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Interacao
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.InteracaoOrm

fun Atendimento.toAtendimentoOrm(now: AppDateTime) = AtendimentoOrm(
    id = id,
    createDateTime = createDateTime ?: now,
    updateDateTime = now,
    searchedStartDate = preferredPeriod?.startDate,
    searchedEndDate = preferredPeriod?.endDate,
    hasFlexibilityForPeriod = hasFlexibilityForPeriod,
    maxAcceptedNightlyRate = maxAcceptedNightlyRate,
    adults = adultsCount.value,
    kids = kidsCount.value,
    babies = babiesCount.value,
    animals = animalsCount.value,
    bedrooms = bedroomsCount.value,
    enSuites = enSuitesCount.value,
    doubleBeds = doubleBedsCount.value,
    singleBeds = singleBedsCount.value,
    bathrooms = bathroomsCount.value,
    parkingSpots = parkingSpotsCount.value,
    imovelSeaProximityTypeId = preferredSeaProximityType?.id,
    distanceToSea = (preferredSeaProximityType as? ImovelSeaProximityType.DistanceToSea)?.distance,
    observations = observations,
    isFinished = isFinished,
    isDeleted = false,
    clienteId = clienteId,
    contractVersion = BuildConfig.CONTRACT_VERSION,
)

fun Interacao.toInteracaoOrm(atendimentoId: Long) = InteracaoOrm(
    id = id,
    date = date,
    title = title,
    description = description,
    atendimentoId = atendimentoId,
)
