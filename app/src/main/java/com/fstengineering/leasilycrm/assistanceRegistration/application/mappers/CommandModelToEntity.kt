package com.fstengineering.leasilycrm.assistanceRegistration.application.mappers

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.Assistance
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.toPersistentList

fun AssistanceCommandModel.toEntity(): AppResultCompound<Assistance, DomainError> {
    val errors = mutableListOf<DomainError>()

    val isAnyNeighborhoodEmpty = preferredNeighborhoods.any { it.isBlank() }
    if (isAnyNeighborhoodEmpty) errors.add(DomainError.Neighborhood.Required)

    val interactions = interactions
        .mapNotNull { interaction ->
            interaction.toEntity()
                .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })
        }
        .toPersistentList()

    val parsed = Assistance.parse(
        id = id,
        createDateTime = createDateTime,
        preferredStartDate = preferredStartDate,
        preferredEndDate = preferredEndDate,
        hasFlexibilityForPeriod = hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = maxAcceptedNightlyRate,
        adultsCount = adultsCount,
        kidsCount = kidsCount,
        babiesCount = babiesCount,
        animalsCount = animalsCount,
        bedroomsCount = bedroomsCount,
        enSuitesCount = enSuitesCount,
        doubleBedsCount = doubleBedsCount,
        singleBedsCount = singleBedsCount,
        bathroomsCount = bathroomsCount,
        parkingSpotsCount = parkingSpotsCount,
        preferredNeighborhoods = preferredNeighborhoods,
        preferredSeaProximityTypeId = preferredSeaProximityTypeId,
        preferredDistanceToSea = preferredDistanceToSea,
        preferredPropertyCharacteristics = preferredPropertyCharacteristics,
        observations = observations,
        sentPropertiesIds = sentPropertiesIds,
        interactions = interactions,
        leadTenantId = leadTenantId,
        isFinished = false,
    )
        .fold(onErrors = { errors.addAll(0, it); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

    return AppResultCompound.Success(data = requireNotNull(parsed))
}

private fun AssistanceInteractionCommandModel.toEntity() = AssistanceInteraction.parse(
    id = id,
    date = date,
    title = title,
    description = description,
)
