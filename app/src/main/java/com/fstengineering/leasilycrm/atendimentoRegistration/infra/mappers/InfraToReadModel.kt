package com.fstengineering.leasilycrm.atendimentoRegistration.infra.mappers

import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoSelectableImovelReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.ImovelEnviadoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.InteracaoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.projections.AtendimentoRegistrationProjection
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.projections.ImovelEnviadoProjection
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.projections.SelectableImovelProjection
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.InteracaoOrm
import kotlinx.collections.immutable.toPersistentList

fun AtendimentoRegistrationProjection.toReadModel() = AtendimentoReadModel(
    id = requireNotNull(atendimento.id),
    clienteId = atendimento.clienteId,
    clienteName = clienteName,
    createDateTime = atendimento.createDateTime,
    preferredStartDate = atendimento.searchedStartDate,
    preferredEndDate = atendimento.searchedEndDate,
    hasFlexibilityForPeriod = atendimento.hasFlexibilityForPeriod,
    maxAcceptedNightlyRate = atendimento.maxAcceptedNightlyRate,
    adultsCount = atendimento.adults,
    kidsCount = atendimento.kids,
    babiesCount = atendimento.babies,
    animalsCount = atendimento.animals,
    bedroomsCount = atendimento.bedrooms,
    enSuitesCount = atendimento.enSuites,
    doubleBedsCount = atendimento.doubleBeds,
    singleBedsCount = atendimento.singleBeds,
    bathroomsCount = atendimento.bathrooms,
    parkingSpotsCount = atendimento.parkingSpots,
    preferredSeaProximityTypeId = atendimento.imovelSeaProximityTypeId,
    preferredDistanceToSea = atendimento.distanceToSea,
    preferredNeighborhoods = preferredNeighborhoods.map { it.neighborhood }.toPersistentList(),
    preferredImovelCharacteristics = preferredImovelCharacteristics
        .mapNotNull { ImovelCharacteristic.fromId(it.imovelCharacteristicId) }
        .toPersistentList(),
    observations = atendimento.observations,
    imoveisEnviados = imoveisEnviados
        .filterNot { it.isDeleted }
        .map { it.toReadModel() }
        .toPersistentList(),
    interacoes = interacoes.map { it.toReadModel() }.toPersistentList(),
)

fun SelectableImovelProjection.toReadModel() = AtendimentoSelectableImovelReadModel(
    id = id,
    type = requireNotNull(ImovelType.fromId(typeId)),
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    number = number,
    seaProximityTypeId = seaProximityTypeId,
    distanceToSea = distanceToSea?.toString(),
    bedroomsCount = bedrooms,
    mainPictureUriString = mainPictureUriString,
)

private fun ImovelEnviadoProjection.toReadModel() = ImovelEnviadoReadModel(
    id = id,
    type = requireNotNull(ImovelType.fromId(typeId)),
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    number = number,
    seaProximityTypeId = seaProximityTypeId,
    distanceToSea = distanceToSea?.toString(),
    bedroomsCount = bedrooms,
    mainPictureUriString = files.find { it.isMainPicture }?.uriString,
)

private fun InteracaoOrm.toReadModel() = InteracaoReadModel(
    id = requireNotNull(id),
    date = date,
    title = title,
    description = description,
)
