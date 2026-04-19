package com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import kotlinx.collections.immutable.ImmutableList

data class AssistanceReadModel(
    val id: Long,
    val updateDateTime: AppDateTime,
    val searchedStartDate: AppDate?,
    val searchedEndDate: AppDate?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val adults: Int,
    val kids: Int,
    val babies: Int,
    val animals: Int,
    val bedrooms: Int,
    val enSuites: Int,
    val doubleBeds: Int,
    val singleBeds: Int,
    val bathrooms: Int,
    val parkingSpots: Int,
    val preferredSeaProximityType: PropertySeaProximityType?,
    val distanceToSea: AppDecimal?,
    val observations: String?,
    val isFinished: Boolean,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    val interactions: ImmutableList<AssistanceInteractionReadModel>,
    val sentProperties: ImmutableList<SentPropertyReadModel>,
)
