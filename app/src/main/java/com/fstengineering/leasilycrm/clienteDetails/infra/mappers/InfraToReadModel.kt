package com.fstengineering.leasilycrm.clienteDetails.infra.mappers

import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceSentImovelReadModel
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
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
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
    assistances = atendimentos
        .filterNot { it.assistance.isDeleted }
        .map { it.toReadModel() }
        .toPersistentList(),
    stays = estadias
        .filterNot { it.isDeleted }
        .map { it.toReadModel() }
        .toPersistentList(),
)

private fun AtendimentoProjection.toReadModel(): AssistanceReadModel {
    val preferredNeighborhoods = preferredNeighborhoods
        .map { it.neighborhood }
        .toPersistentList()

    val preferredCharacteristics = preferredImovelCharacteristics
        .mapNotNull { ImovelCharacteristic.fromId(it.imovelCharacteristicId) }
        .toPersistentList()

    val interactionReadModels = interactions
        .map { it.toReadModel() }
        .toPersistentList()

    val seaProximityType = assistance.imovelSeaProximityTypeId?.let { typeId ->
        ImovelSeaProximityType.parse(typeId, assistance.distanceToSea)
            .fold(onError = { null }, onSuccess = { it })
    }

    return AssistanceReadModel(
        id = requireNotNull(assistance.id),
        updateDateTime = assistance.updateDateTime,
        preferredStartDate = assistance.searchedStartDate,
        preferredEndDate = assistance.searchedEndDate,
        hasFlexibilityForPeriod = assistance.hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = assistance.maxAcceptedNightlyRate,
        adultsCount = assistance.adults,
        kidsCount = assistance.kids,
        babiesCount = assistance.babies,
        animalsCount = assistance.animals,
        bedroomsCount = assistance.bedrooms,
        enSuitesCount = assistance.enSuites,
        doubleBedsCount = assistance.doubleBeds,
        singleBedsCount = assistance.singleBeds,
        bathroomsCount = assistance.bathrooms,
        parkingSpotsCount = assistance.parkingSpots,
        preferredSeaProximityTypeId = seaProximityType?.id,
        preferredDistanceToSea = assistance.distanceToSea,
        observations = assistance.observations,
        isFinished = assistance.isFinished,
        preferredNeighborhoods = preferredNeighborhoods,
        preferredImovelCharacteristics = preferredCharacteristics,
        interactions = interactionReadModels,
        sentImoveis = imoveisEnviados
            .filterNot { it.isDeleted }
            .map { it.toReadModel() }
            .toPersistentList(),
    )
}

private fun AssistanceInteractionEntity.toReadModel() =
    AssistanceInteractionReadModel(
        id = requireNotNull(id),
        date = date,
        title = title,
        description = description,
    )

private fun ImovelEnviadoProjection.toReadModel() = AssistanceSentImovelReadModel(
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
