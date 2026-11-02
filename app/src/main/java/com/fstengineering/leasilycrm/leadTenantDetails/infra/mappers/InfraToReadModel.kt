package com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers

import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantRawReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.StayReadModel
import kotlinx.collections.immutable.toPersistentList

fun LeadTenantReadView.toRawReadModel() = LeadTenantRawReadModel(
    id = requireNotNull(id),
    tag = tag,
    name = name,
    city = city,
    identityDocument = identityDocument,
    phone = phone,
    email = email,
    contactOrigin = contactOrigin,
    observations = observations,
    status = status,
)

fun AssistanceCompound.toRawReadModel(): AssistanceRawReadModel {
    val preferredNeighborhoods = preferredNeighborhoods
        .map { it.neighborhood }
        .toPersistentList()

    val preferredCharacteristics = preferredPropertyCharacteristics
        .mapNotNull { ImovelCharacteristic.fromId(it.imovelCharacteristicId) }
        .toPersistentList()

    val interactionReadModels = interactions
        .map { it.toReadModel() }
        .toPersistentList()

    val seaProximityType = assistance.propertySeaProximityTypeId?.let { typeId ->
        ImovelSeaProximityType.parse(typeId, assistance.distanceToSea)
            .fold(onError = { null }, onSuccess = { it })
    }

    return AssistanceRawReadModel(
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
        preferredPropertyCharacteristics = preferredCharacteristics,
        interactions = interactionReadModels,
    )
}

fun AssistanceInteractionEntity.toReadModel() = AssistanceInteractionReadModel(
    id = requireNotNull(id),
    date = date,
    title = title,
    description = description,
)

fun ImovelCompound.toSentPropertyReadModel() = AssistanceSentPropertyReadModel(
    id = requireNotNull(imovel.id),
    mainPictureUriString = files.find { it.isMainPicture }?.imovelFileUri,
    neighborhood = imovel.neighborhood,
    streetAddress = imovel.streetAddress,
    number = imovel.number,
    type = requireNotNull(ImovelType.fromId(imovel.typeId)),
)

fun StayEntity.toReadModel() = StayReadModel(
    id = requireNotNull(id),
    checkInDateTime = checkInDateTime,
    isCheckOutDone = status == 2,
)
