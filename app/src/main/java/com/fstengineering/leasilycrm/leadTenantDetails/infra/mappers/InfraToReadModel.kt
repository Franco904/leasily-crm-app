package com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers

import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantRawReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.SentPropertyReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.StayReadModel
import kotlinx.collections.immutable.ImmutableList
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

fun AssistanceCompound.toReadModel(sentProperties: ImmutableList<SentPropertyReadModel>): AssistanceReadModel {
    val preferredNeighborhoods = preferredNeighborhoods
        .map { it.neighborhood }
        .toPersistentList()

    val preferredCharacteristics = preferredPropertyCharacteristics
        .mapNotNull { PropertyCharacteristic.fromId(it.propertyCharacteristicId) }
        .toPersistentList()

    val interactionReadModels = interactions
        .map { it.toReadModel() }
        .toPersistentList()

    val seaProximityType = assistance.propertySeaProximityTypeId?.let { typeId ->
        PropertySeaProximityType.parse(typeId, assistance.distanceToSea)
            .fold(onErrors = { null }, onSuccess = { it })
    }

    return AssistanceReadModel(
        id = requireNotNull(assistance.id),
        updateDateTime = assistance.updateDateTime,
        searchedStartDate = assistance.searchedStartDate,
        searchedEndDate = assistance.searchedEndDate,
        hasFlexibilityForPeriod = assistance.hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = assistance.maxAcceptedNightlyRate,
        adults = assistance.adults,
        kids = assistance.kids,
        babies = assistance.babies,
        animals = assistance.animals,
        bedrooms = assistance.bedrooms,
        enSuites = assistance.enSuites,
        doubleBeds = assistance.doubleBeds,
        singleBeds = assistance.singleBeds,
        bathrooms = assistance.bathrooms,
        parkingSpots = assistance.parkingSpots,
        preferredSeaProximityType = seaProximityType,
        distanceToSea = assistance.distanceToSea,
        observations = assistance.observations,
        isFinished = assistance.isFinished,
        preferredNeighborhoods = preferredNeighborhoods,
        preferredPropertyCharacteristics = preferredCharacteristics,
        interactions = interactionReadModels,
        sentProperties = sentProperties,
    )
}

fun AssistanceInteractionEntity.toReadModel() = AssistanceInteractionReadModel(
    id = requireNotNull(id),
    date = date,
    title = title,
    description = description,
)

fun PropertyCompound.toSentPropertyReadModel() = SentPropertyReadModel(
    id = requireNotNull(property.id),
    mainPictureUriString = files.find { it.isMainPicture }?.propertyFileUri,
    neighborhood = property.neighborhood,
    streetAddress = property.streetAddress,
    number = property.number,
    type = requireNotNull(PropertyType.fromId(property.typeId)),
)

fun StayEntity.toReadModel() = StayReadModel(
    id = requireNotNull(id),
    checkInDateTime = checkInDateTime,
    isCheckOutDone = status == 2,
)
