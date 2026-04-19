package com.fstengineering.leasilycrm.leadTenantDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class AssistanceUiState(
    val id: Long,
    val updateDateTime: AppDateTime,
    val searchingStartDate: AppDate?,
    val searchingEndDate: AppDate?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val qtAdultGuests: Int,
    val qtKidGuests: Int,
    val qtBabyGuests: Int,
    val qtAnimalGuests: Int,
    val qtBedrooms: Int,
    val qtEnSuites: Int,
    val qtDoubleBeds: Int,
    val qtSingleBeds: Int,
    val qtBathrooms: Int,
    val qtParkingSpots: Int,
    val preferredSeaProximityTypeId: Long?,
    val distanceToSea: AppDecimal?,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    val observations: String?,
    val lastSentProperty: PropertyCardUiState?,
    val sentProperties: ImmutableList<PropertyCardUiState>,
    val lastInteraction: AssistanceInteractionUiState?,
    val interactionsByMonth: ImmutableMap<AppYearMonth, List<AssistanceInteractionUiState>>,
    val isFinished: Boolean,
)
