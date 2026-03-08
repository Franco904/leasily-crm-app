package com.fstengineering.leasilycrm.assistanceRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.Assistance
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity

fun Assistance.toLocalDataModel(): AssistanceEntity {
    return AssistanceEntity(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        searchedStartDate = preferredPeriod?.startDate,
        searchedEndDate = preferredPeriod?.endDate,
        hasFlexibilityForPeriod = hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = maxAcceptedNightlyRate,
        adults = guestsCount.adultsCount,
        kids = guestsCount.kidsCount,
        babies = guestsCount.babiesCount,
        animals = guestsCount.animalsCount,
        bedrooms = preferredFeaturesCount.bedroomsCount,
        enSuites = preferredFeaturesCount.enSuitesCount,
        doubleBeds = preferredFeaturesCount.doubleBedsCount,
        singleBeds = preferredFeaturesCount.singleBedsCount,
        bathrooms = preferredFeaturesCount.bathroomsCount,
        parkingSpots = preferredFeaturesCount.parkingSpotsCount,
        propertySeaProximityTypeId = preferredSeaProximityType?.id,
        distanceToSea = (preferredSeaProximityType as? PropertySeaProximityType.DistanceToSea)?.distance,
        observations = observations,
        isFinished = isFinished,
        isDeleted = false,
        tenantId = leadTenantId,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}

fun AssistanceInteraction.toLocalDataModel(
    assistanceId: Long,
): AssistanceInteractionEntity {
    return AssistanceInteractionEntity(
        id = id,
        date = date,
        title = title,
        description = description,
        assistanceId = assistanceId,
    )
}
