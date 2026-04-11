package com.fstengineering.leasilycrm.assistanceRegistration.domain.entities

import com.fstengineering.leasilycrm.assistanceRegistration.domain.valueObjects.AssistanceGuestsCount
import com.fstengineering.leasilycrm.assistanceRegistration.domain.valueObjects.AssistancePreferredFeaturesCount
import com.fstengineering.leasilycrm.assistanceRegistration.domain.valueObjects.AssistancePreferredPeriod
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class Assistance private constructor(
    val id: Long?,
    val leadTenantId: Long,
    val createDateTime: AppDateTime?,
    val preferredPeriod: AssistancePreferredPeriod?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val guestsCount: AssistanceGuestsCount,
    val preferredFeaturesCount: AssistancePreferredFeaturesCount,
    val preferredSeaProximityType: PropertySeaProximityType?,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    val observations: String?,
    val sentPropertiesIds: ImmutableList<Long>,
    val interactions: ImmutableList<AssistanceInteraction>,
    val isFinished: Boolean,
) {
    companion object {
        fun parse(
            id: Long?,
            leadTenantId: Long?,
            createDateTime: AppDateTime?,
            preferredStartDate: AppDate?,
            preferredEndDate: AppDate?,
            hasFlexibilityForPeriod: Boolean,
            maxAcceptedNightlyRate: AppDecimal?,
            adultsCount: Int,
            kidsCount: Int,
            babiesCount: Int,
            animalsCount: Int,
            bedroomsCount: Int,
            enSuitesCount: Int,
            doubleBedsCount: Int,
            singleBedsCount: Int,
            bathroomsCount: Int,
            parkingSpotsCount: Int,
            preferredSeaProximityTypeId: Long?,
            preferredDistanceToSea: AppDecimal?,
            preferredNeighborhoods: ImmutableList<String>,
            preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
            observations: String?,
            sentPropertiesIds: ImmutableList<Long>,
            interactions: ImmutableList<AssistanceInteraction>,
            isFinished: Boolean,
        ): AppResultCompound<Assistance, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (leadTenantId == null) errors.add(DomainError.AssistanceOtherFields.TenantRequired)

            val preferredPeriod =
                if (preferredStartDate == null || preferredEndDate == null) null
                else AssistancePreferredPeriod.parse(
                    startDate = preferredStartDate,
                    endDate = preferredEndDate,
                )
                    .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val guestsCount = AssistanceGuestsCount.parse(
                adultsCount = adultsCount,
                kidsCount = kidsCount,
                babiesCount = babiesCount,
                animalsCount = animalsCount,
            )
                .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val preferredFeaturesCount = AssistancePreferredFeaturesCount.parse(
                bedroomsCount = bedroomsCount,
                enSuitesCount = enSuitesCount,
                doubleBedsCount = doubleBedsCount,
                singleBedsCount = singleBedsCount,
                bathroomsCount = bathroomsCount,
                parkingSpotsCount = parkingSpotsCount,
            )
                .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val preferredSeaProximityType = preferredSeaProximityTypeId?.let { typeId ->
                PropertySeaProximityType.parse(typeId, distanceToSea = preferredDistanceToSea)
                    .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })
            }

            val isAnyNeighborhoodEmpty = preferredNeighborhoods.any { it.isBlank() }
            if (isAnyNeighborhoodEmpty) errors.add(DomainError.Neighborhood.Required)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = Assistance(
                id = id,
                createDateTime = createDateTime,
                preferredPeriod = preferredPeriod,
                hasFlexibilityForPeriod = hasFlexibilityForPeriod,
                maxAcceptedNightlyRate = maxAcceptedNightlyRate,
                guestsCount = requireNotNull(guestsCount),
                preferredFeaturesCount = requireNotNull(preferredFeaturesCount),
                preferredSeaProximityType = preferredSeaProximityType,
                isFinished = isFinished,
                preferredNeighborhoods = preferredNeighborhoods,
                preferredPropertyCharacteristics = preferredPropertyCharacteristics,
                observations = observations,
                sentPropertiesIds = sentPropertiesIds,
                interactions = interactions,
                leadTenantId = requireNotNull(leadTenantId),
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
