package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.AssistanceGuestsCount
import com.fstengineering.leasilycrm.core.domain.valueObjects.AssistancePreferredFeaturesCount
import com.fstengineering.leasilycrm.core.domain.valueObjects.AssistanceSearchedPeriod
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import kotlinx.collections.immutable.ImmutableList

class Assistance private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val searchedPeriod: AssistanceSearchedPeriod?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val guestsCount: AssistanceGuestsCount,
    val preferredFeaturesCount: AssistancePreferredFeaturesCount,
    val seaProximityType: PropertySeaProximityType?,
    val isFinished: Boolean,
    val isDeleted: Boolean,
    val searchedNeighborhoods: ImmutableList<AssistanceSearchedNeighborhood>,
    val characteristics: ImmutableList<AssistancePropertyCharacteristic>,
    val observations: String?,
    val sentProperties: ImmutableList<AssistanceSentProperty>,
    val interactions: ImmutableList<AssistanceInteraction>,
    val tenantId: Long,
    val inconsistencies: String?,
    val contractVersion: Int,
) {
    fun finish() = copyWith(isFinished = true)

    fun unfinish() = copyWith(isFinished = false)

    private fun copyWith(
        id: Long? = this.id,
        createDateTime: AppDateTime? = this.createDateTime,
        updateDateTime: AppDateTime? = this.updateDateTime,
        searchedPeriod: AssistanceSearchedPeriod? = this.searchedPeriod,
        hasFlexibilityForPeriod: Boolean = this.hasFlexibilityForPeriod,
        maxAcceptedNightlyRate: AppDecimal? = this.maxAcceptedNightlyRate,
        guestsCount: AssistanceGuestsCount = this.guestsCount,
        preferredFeaturesCount: AssistancePreferredFeaturesCount = this.preferredFeaturesCount,
        seaProximityType: PropertySeaProximityType? = this.seaProximityType,
        isFinished: Boolean = this.isFinished,
        isDeleted: Boolean = this.isDeleted,
        searchedNeighborhoods: ImmutableList<AssistanceSearchedNeighborhood> = this.searchedNeighborhoods,
        characteristics: ImmutableList<AssistancePropertyCharacteristic> = this.characteristics,
        observations: String? = this.observations,
        sentProperties: ImmutableList<AssistanceSentProperty> = this.sentProperties,
        interactions: ImmutableList<AssistanceInteraction> = this.interactions,
        tenantId: Long = this.tenantId,
        inconsistencies: String? = this.inconsistencies,
        contractVersion: Int = this.contractVersion,
    ) = Assistance(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        searchedPeriod = searchedPeriod,
        hasFlexibilityForPeriod = hasFlexibilityForPeriod,
        maxAcceptedNightlyRate = maxAcceptedNightlyRate,
        guestsCount = guestsCount,
        preferredFeaturesCount = preferredFeaturesCount,
        seaProximityType = seaProximityType,
        isFinished = isFinished,
        isDeleted = isDeleted,
        searchedNeighborhoods = searchedNeighborhoods,
        characteristics = characteristics,
        observations = observations,
        sentProperties = sentProperties,
        interactions = interactions,
        tenantId = tenantId,
        inconsistencies = inconsistencies,
        contractVersion = contractVersion,
    )

    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            searchedStartDate: AppDate?,
            searchedEndDate: AppDate?,
            hasFlexibilityForPeriod: Boolean,
            maxAcceptedNightlyRate: AppDecimal?,
            adults: Int,
            kids: Int,
            babies: Int,
            animals: Int,
            bedrooms: Int,
            enSuites: Int,
            doubleBeds: Int,
            singleBeds: Int,
            bathrooms: Int,
            parkingSpots: Int,
            seaProximityTypeId: Long?,
            distanceToSea: AppDecimal?,
            isFinished: Boolean,
            isDeleted: Boolean,
            searchedNeighborhoods: ImmutableList<AssistanceSearchedNeighborhood>,
            characteristics: ImmutableList<AssistancePropertyCharacteristic>,
            observations: String?,
            sentProperties: ImmutableList<AssistanceSentProperty>,
            interactions: ImmutableList<AssistanceInteraction>,
            tenantId: Long,
            inconsistencies: String?,
            contractVersion: Int,
        ): Assistance {
            val searchedPeriod =
                if (searchedStartDate == null && searchedEndDate == null) null else {
                    AssistanceSearchedPeriod.parse(
                        startDate = searchedStartDate,
                        endDate = searchedEndDate,
                    )
                }

            val guestsCount = AssistanceGuestsCount.parse(
                adults = adults,
                kids = kids,
                babies = babies,
                animals = animals,
            )

            val preferredFeaturesCount = AssistancePreferredFeaturesCount.parse(
                bedrooms = bedrooms,
                enSuites = enSuites,
                doubleBeds = doubleBeds,
                singleBeds = singleBeds,
                bathrooms = bathrooms,
                parkingSpots = parkingSpots,
            )

            val seaProximityType = seaProximityTypeId?.let {
                PropertySeaProximityType.parse(it, distanceToSea = distanceToSea)
            }

            return Assistance(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                searchedPeriod = searchedPeriod,
                hasFlexibilityForPeriod = hasFlexibilityForPeriod,
                maxAcceptedNightlyRate = maxAcceptedNightlyRate,
                guestsCount = guestsCount,
                preferredFeaturesCount = preferredFeaturesCount,
                seaProximityType = seaProximityType,
                isFinished = isFinished,
                isDeleted = isDeleted,
                searchedNeighborhoods = searchedNeighborhoods,
                characteristics = characteristics,
                observations = observations,
                sentProperties = sentProperties,
                interactions = interactions,
                tenantId = tenantId,
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )
        }
    }
}

//val isAnyFormFieldInitialized = searchedStartDate != null
//        || searchedEndDate != null
//        || hasFlexibilityForPeriod
//        || maxAcceptedNightlyRate != null
//        || qtAdultGuests > 0
//        || qtKidGuests > 0
//        || qtBabyGuests > 0
//        || qtAnimalGuests > 0
//        || qtBedrooms > 0
//        || qtEnSuites > 0
//        || qtDoubleBeds > 0
//        || qtSingleBeds > 0
//        || qtBathrooms > 0
//        || qtParkingSpots > 0
//        || seaProximityType != null
//        || distanceToSea != null
//        || searchedNeighborhoods != null
//        || characteristics != null
//        || observations != null
//        || sentProperties != null
//        || interactions != null
