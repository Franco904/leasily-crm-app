package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardDetailsUiState
import kotlinx.collections.immutable.ImmutableList

sealed interface AssistanceDraftFormUiState {
    data object Loading : AssistanceDraftFormUiState

    data class Loaded(
        val id: Long? = null,
        val createDateTime: AppDateTime? = null,
        val preferredStartDate: AppDate? = null,
        val preferredEndDate: AppDate? = null,
        val hasFlexibilityForPeriod: Boolean,
        val maxAcceptedNightlyRate: AppDecimal? = null,
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
        val preferredSeaProximityTypeId: Long? = null,
        val preferredDistanceToSea: AppDecimal? = null,
        val preferredPropertyCharacteristics: ImmutableList<PropertyCharacteristic>,
        val observations: String? = null,
        val sentProperties: ImmutableList<PropertyCardDetailsUiState>,
        val interactions: ImmutableList<AssistanceInteractionDraftFormUiState.Loaded>,
        val leadTenantId: Long,
        val leadTenantName: String,
    ) : AssistanceDraftFormUiState
}
