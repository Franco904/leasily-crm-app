package com.fstengineering.leasilycrm.leadTenantDetails.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.SentPropertyReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.StayReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.AssistanceInteractionUiState
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.AssistanceUiState
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.LeadTenantDetailsUiState
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.LeadTenantHistoryItemUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun LeadTenantReadModel.toUiState(): LeadTenantDetailsUiState.Loaded {
    val currentStayId = stays.find { !it.isCheckOutDone }?.id

    val allAssistancesUiState = assistances.map { it.toUiState() }.toPersistentList()
    val currentAssistance = allAssistancesUiState.find { !it.isFinished }
    val pastAssistances = allAssistancesUiState.filter { it.isFinished }.toPersistentList()

    val historyByYear = buildHistory(
        assistances = pastAssistances,
        stays = stays,
    )

    return LeadTenantDetailsUiState.Loaded(
        id = id,
        tag = tag,
        name = name,
        city = city,
        identityDocument = identityDocument,
        phone = phone,
        email = email,
        contactOrigin = contactOrigin,
        observations = observations,
        isLead = isLead,
        isOld = isOld,
        currentStayId = currentStayId,
        currentAssistance = currentAssistance,
        pastAssistances = pastAssistances,
        historyByYear = historyByYear,
    )
}

private fun buildHistory(
    assistances: ImmutableList<AssistanceUiState>,
    stays: List<StayReadModel>,
): ImmutableMap<Int, ImmutableList<LeadTenantHistoryItemUiState>> {
    val assistanceItems = assistances.map {
        LeadTenantHistoryItemUiState(
            id = it.id,
            isAssistance = true,
            dateTime = it.updateDateTime,
        )
    }

    val stayItems = stays
        .filter { it.isCheckOutDone }
        .map {
            LeadTenantHistoryItemUiState(
                id = it.id,
                isAssistance = false,
                dateTime = it.checkInDateTime,
            )
        }

    return (assistanceItems + stayItems)
        .sortedByDescending { it.dateTime }
        .groupBy { it.dateTime.year }
        .mapValues { (_, items) -> items.toPersistentList() }
        .toPersistentMap()
}

private fun AssistanceReadModel.toUiState(): AssistanceUiState {
    val sortedInteractions = interactions
        .map { it.toUiState() }
        .sortedByDescending { it.date }
        .groupBy { interaction ->
            AppYearMonth.of(
                year = interaction.date.year,
                month = interaction.date.month,
            )
        }
        .toPersistentMap()

    val sortedSentProperties = sentProperties
        .map { it.toUiState() }
        .toPersistentList()

    return AssistanceUiState(
        id = id,
        updateDateTime = updateDateTime,
        searchingStartDate = searchedStartDate,
        searchingEndDate = searchedEndDate,
        hasFlexibilityForPeriod = hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = maxAcceptedNightlyRate,
        qtAdultGuests = adults,
        qtKidGuests = kids,
        qtBabyGuests = babies,
        qtAnimalGuests = animals,
        qtBedrooms = bedrooms,
        qtEnSuites = enSuites,
        qtDoubleBeds = doubleBeds,
        qtSingleBeds = singleBeds,
        qtBathrooms = bathrooms,
        qtParkingSpots = parkingSpots,
        preferredSeaProximityTypeId = preferredSeaProximityType?.id,
        distanceToSea = distanceToSea,
        preferredNeighborhoods = preferredNeighborhoods,
        preferredPropertyCharacteristics = preferredPropertyCharacteristics,
        observations = observations,
        lastSentProperty = sortedSentProperties.firstOrNull(),
        sentProperties = sortedSentProperties,
        lastInteraction = sortedInteractions.values.flatten().firstOrNull(),
        interactionsByMonth = sortedInteractions,
        isFinished = isFinished,
    )
}

private fun AssistanceInteractionReadModel.toUiState() = AssistanceInteractionUiState(
    id = id,
    date = date,
    title = title,
    description = description,
)

private fun SentPropertyReadModel.toUiState(): PropertyCardUiState {
    val streetAddressAndNumber = buildString {
        append(streetAddress)
        if (number != null) append(", $number")
    }

    return PropertyCardUiState(
        id = id,
        mainPictureUri = mainPictureUriString?.toUri(),
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
    )
}
