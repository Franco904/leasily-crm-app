package com.fstengineering.leasilycrm.clienteDetails.infra.mappers

import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.AssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.application.models.StayReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.projections.ClienteDetailsProjection
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

fun ClienteDetailsProjection.toClienteReadModel(
    assistances: ImmutableList<AssistanceReadModel>,
    stays: ImmutableList<StayReadModel>,
) = ClienteReadModel(
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
    assistances = assistances,
    stays = stays,
)

fun AssistanceCompound.toAssistanceReadModel(
    sentImoveis: ImmutableList<AssistanceSentImovelReadModel>,
): AssistanceReadModel {
    val preferredNeighborhoods = preferredNeighborhoods
        .map { it.neighborhood }
        .toPersistentList()

    val preferredCharacteristics = preferredImovelCharacteristics
        .mapNotNull { ImovelCharacteristic.fromId(it.imovelCharacteristicId) }
        .toPersistentList()

    val interactionReadModels = interactions
        .map { it.toAssistanceInteractionReadModel() }
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
        sentImoveis = sentImoveis,
    )
}

private fun AssistanceInteractionEntity.toAssistanceInteractionReadModel() =
    AssistanceInteractionReadModel(
        id = requireNotNull(id),
        date = date,
        title = title,
        description = description,
    )

fun ImovelCompound.toAssistanceSentImovelReadModel() = AssistanceSentImovelReadModel(
    id = requireNotNull(imovel.id),
    mainPictureUriString = files.find { it.isMainPicture }?.imovelFileUri,
    neighborhood = imovel.neighborhood,
    streetAddress = imovel.streetAddress,
    number = imovel.number,
    type = requireNotNull(ImovelType.fromId(imovel.typeId)),
)

fun StayEntity.toStayReadModel() = StayReadModel(
    id = requireNotNull(id),
    checkInDateTime = checkInDateTime,
    isCheckOutDone = status == 2,
)
