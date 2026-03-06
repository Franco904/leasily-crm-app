package com.fstengineering.leasilycrm.assistanceRegistration.infra.utils

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceRawReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceSelectablePropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun AssistanceCompound.toAssistanceRawReadModel(): AssistanceRawReadModel {
    val preferredNeighborhoodsNames = preferredNeighborhoods
        .map { it.neighborhood }
        .toPersistentList()

    val preferredCharacteristics = preferredPropertyCharacteristics
        .mapNotNull { PropertyCharacteristic.fromId(it.propertyCharacteristicId) }
        .toPersistentList()

    val interactions = interactions
        .map { it.toReadModel() }
        .toPersistentList()

    return AssistanceRawReadModel(
        id = assistance.id,
        createDateTime = assistance.createDateTime,
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
        preferredSeaProximityTypeId = assistance.propertySeaProximityTypeId,
        preferredDistanceToSea = assistance.distanceToSea,
        observations = assistance.observations,
        isFinished = assistance.isFinished,
        preferredNeighborhoods = preferredNeighborhoodsNames,
        preferredPropertyCharacteristics = preferredCharacteristics,
        interactions = interactions,
        inconsistencies = persistentListOf(),
        contractVersion = assistance.contractVersion,
    )
}

private fun AssistanceInteractionEntity.toReadModel() = AssistanceInteractionReadModel(
    id = id,
    date = date,
    title = title,
    description = description,
)

fun PropertyCompound.toAssistanceSentPropertyReadModel(): AssistanceSentPropertyReadModel {
    return AssistanceSentPropertyReadModel(
        id = property.id,
        type = requireNotNull(PropertyType.fromId(property.typeId)),
        neighborhood = property.neighborhood,
        streetAddress = property.streetAddress,
        number = property.number,
        seaProximityTypeId = property.seaProximityTypeId,
        distanceToSea = property.distanceToSea?.toString(),
        bedroomsCount = property.bedrooms,
        mainPictureUri = files
            .find { picture -> picture.isMainPicture }
            ?.propertyFileUri?.toUri()
    )
}

fun PropertyCompound.toAssistanceSelectablePropertyReadModel(): AssistanceSelectablePropertyReadModel {
    return AssistanceSelectablePropertyReadModel(
        id = property.id,
        type = requireNotNull(PropertyType.fromId(property.typeId)),
        neighborhood = property.neighborhood,
        streetAddress = property.streetAddress,
        number = property.number,
        seaProximityTypeId = property.seaProximityTypeId,
        distanceToSea = property.distanceToSea?.toString(),
        bedroomsCount = property.bedrooms,
        mainPictureUri = files
            .find { picture -> picture.isMainPicture }
            ?.propertyFileUri?.toUri()
    )
}
