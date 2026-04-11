package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import kotlinx.collections.immutable.ImmutableList

sealed interface AssistanceDraftFormUiState {
    data object Loading : AssistanceDraftFormUiState

    data class Loaded(
        val id: Long?,
        val createDateTime: AppDateTime?,
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
        val sentProperties: ImmutableList<PropertyCardUiState>,
        val interactions: ImmutableList<AssistanceInteractionDraftFormUiState.Loaded>,
        val leadTenantId: Long,
        val leadTenantName: String,
    ) : AssistanceDraftFormUiState
}
