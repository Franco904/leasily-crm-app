package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.Neighborhood
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

data class AssistanceCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val preferredStartDate: AppDate?,
    val preferredEndDate: AppDate?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val adultsCount: Int,
    val kidsCount: Int,
    val babiesCount: Int,
    val animalsCount: Int,
    val bedroomsCount: Int,
    val enSuitesCount: Int,
    val doubleBedsCount: Int,
    val singleBedsCount: Int,
    val bathroomsCount: Int,
    val parkingSpotsCount: Int,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredSeaProximityTypeId: Long?,
    val preferredDistanceToSea: AppDecimal?,
    val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    val observations: String?,
    val sentPropertiesIds: ImmutableList<Long>,
    val interactions: ImmutableList<AssistanceInteractionCommandModel>,
    val leadTenantId: Long?,
) : CommandUseCase.CommandModel {
    fun toDomain(): AppResultCompound<Assistance, DomainError> {
        val errors = persistentListOf<DomainError>()

        val preferredNeighborhoods = preferredNeighborhoods
            .mapNotNull { neighborhood ->
                Neighborhood.parse(neighborhood)
                    .fold(onError = { errors.add(it); null }, onSuccess = { it })
            }
            .toImmutableList()

        val interactions = interactions
            .mapNotNull { interaction ->
                interaction.toDomain()
                    .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })
            }
            .toImmutableList()

        val parsed = Assistance.parse(
            id = id,
            createDateTime = createDateTime,
            updateDateTime = updateDateTime,
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
            tenantId = leadTenantId,
            isFinished = false,
            inconsistencies = persistentListOf(),
            contractVersion = BuildConfig.CONTRACT_VERSION,
        )
            .fold(onErrors = { errors.addAll(0, it); null }, onSuccess = { it })

        if (errors.isNotEmpty()) return AppResultCompound.Errors(errors)

        return AppResultCompound.Success(data = requireNotNull(parsed))
    }
}
