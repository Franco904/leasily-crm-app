package com.fstengineering.leasilycrm.clienteDetails.infra.mappers

import com.fstengineering.leasilycrm.clienteDetails.application.models.InteracaoReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.ImovelEnviadoReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.StayReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.projections.AtendimentoProjection
import com.fstengineering.leasilycrm.clienteDetails.infra.projections.ClienteDetailsProjection
import com.fstengineering.leasilycrm.clienteDetails.infra.projections.EstadiaProjection
import com.fstengineering.leasilycrm.clienteDetails.infra.projections.ImovelEnviadoProjection
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.InteracaoOrm
import kotlinx.collections.immutable.toPersistentList

fun ClienteDetailsProjection.toReadModel() = ClienteReadModel(
    id = id,
    tag = tag,
    name = name,
    city = city,
    identityDocument = identityDocument,
    phone = phone,
    email = email,
    contactOrigin = contactOrigin,
    observations = observations,
    isLead = status == ClienteStatus.Lead.id,
    isOld = status == ClienteStatus.HospedeAntigo.id,
    atendimentos = atendimentos
        .filterNot { it.atendimento.isDeleted }
        .map { it.toReadModel() }
        .toPersistentList(),
    stays = estadias
        .filterNot { it.isDeleted }
        .map { it.toReadModel() }
        .toPersistentList(),
)

private fun AtendimentoProjection.toReadModel(): AtendimentoReadModel {
    val preferredNeighborhoods = preferredNeighborhoods
        .map { it.neighborhood }
        .toPersistentList()

    val preferredCharacteristics = preferredImovelCharacteristics
        .mapNotNull { ImovelCharacteristic.fromId(it.imovelCharacteristicId) }
        .toPersistentList()

    val interacaoReadModels = interacoes
        .map { it.toReadModel() }
        .toPersistentList()

    val seaProximityType = atendimento.imovelSeaProximityTypeId?.let { typeId ->
        ImovelSeaProximityType.parse(typeId, atendimento.distanceToSea)
            .fold(onError = { null }, onSuccess = { it })
    }

    return AtendimentoReadModel(
        id = requireNotNull(atendimento.id),
        updateDateTime = atendimento.updateDateTime,
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
        preferredSeaProximityTypeId = seaProximityType?.id,
        preferredDistanceToSea = atendimento.distanceToSea,
        observations = atendimento.observations,
        isFinished = atendimento.isFinished,
        preferredNeighborhoods = preferredNeighborhoods,
        preferredImovelCharacteristics = preferredCharacteristics,
        interacoes = interacaoReadModels,
        sentImoveis = imoveisEnviados
            .filterNot { it.isDeleted }
            .map { it.toReadModel() }
            .toPersistentList(),
    )
}

private fun InteracaoOrm.toReadModel() =
    InteracaoReadModel(
        id = requireNotNull(id),
        date = date,
        title = title,
        description = description,
    )

private fun ImovelEnviadoProjection.toReadModel() = ImovelEnviadoReadModel(
    id = id,
    mainPictureUriString = files.find { it.isMainPicture }?.uriString,
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    number = number,
    type = requireNotNull(ImovelType.fromId(typeId)),
)

private fun EstadiaProjection.toReadModel() = StayReadModel(
    id = id,
    checkInDateTime = checkInDateTime,
    isCheckOutDone = status == 2,
)
