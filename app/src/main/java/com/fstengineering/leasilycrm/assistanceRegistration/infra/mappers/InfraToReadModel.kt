package com.fstengineering.leasilycrm.assistanceRegistration.infra.mappers

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSelectablePropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import kotlinx.collections.immutable.toPersistentList

fun AssistanceCompound.toRawReadModel(): AssistanceRawReadModel {
    val preferredNeighborhoodsNames = preferredNeighborhoods
        .map { it.neighborhood }
        .toPersistentList()

    val preferredCharacteristics = preferredPropertyCharacteristics
        .mapNotNull { ImovelCharacteristic.fromId(it.imovelCharacteristicId) }
        .toPersistentList()

    val interactions = interactions
        .map { it.toReadModel() }
        .toPersistentList()

    return AssistanceRawReadModel(
        id = assistance.id,
        createDateTime = assistance.createDateTime,
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
        preferredSeaProximityTypeId = assistance.propertySeaProximityTypeId,
        preferredDistanceToSea = assistance.distanceToSea,
        observations = assistance.observations,
        isFinished = assistance.isFinished,
        preferredNeighborhoods = preferredNeighborhoodsNames,
        preferredPropertyCharacteristics = preferredCharacteristics,
        interactions = interactions,
    )
}

private fun AssistanceInteractionEntity.toReadModel() = AssistanceInteractionReadModel(
    id = id,
    date = date,
    title = title,
    description = description,
)

fun ImovelCompound.toSentPropertyReadModel(): AssistanceSentPropertyReadModel {
    return AssistanceSentPropertyReadModel(
        id = imovel.id,
        type = requireNotNull(ImovelType.fromId(imovel.typeId)),
        neighborhood = imovel.neighborhood,
        streetAddress = imovel.streetAddress,
        number = imovel.number,
        seaProximityTypeId = imovel.seaProximityTypeId,
        distanceToSea = imovel.distanceToSea?.toString(),
        bedroomsCount = imovel.bedrooms,
        mainPictureUriString = files
            .find { picture -> picture.isMainPicture }
            ?.imovelFileUri
    )
}

fun ImovelCompound.toSelectablePropertyReadModel(): AssistanceSelectablePropertyReadModel {
    return AssistanceSelectablePropertyReadModel(
        id = imovel.id,
        type = requireNotNull(ImovelType.fromId(imovel.typeId)),
        neighborhood = imovel.neighborhood,
        streetAddress = imovel.streetAddress,
        number = imovel.number,
        seaProximityTypeId = imovel.seaProximityTypeId,
        distanceToSea = imovel.distanceToSea?.toString(),
        bedroomsCount = imovel.bedrooms,
        mainPictureUriString = files
            .find { picture -> picture.isMainPicture }
            ?.imovelFileUri
    )
}
