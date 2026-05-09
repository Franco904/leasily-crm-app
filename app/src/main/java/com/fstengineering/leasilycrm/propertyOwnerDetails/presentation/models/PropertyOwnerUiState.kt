package com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.models

import kotlinx.collections.immutable.ImmutableList

sealed interface PropertyOwnerUiState {
    data object Loading : PropertyOwnerUiState

    data class Loaded(
        val name: String?,
        val phone: String?,
        val email: String?,
        val paymentKey: String?,
        val files: ImmutableList<PropertyOwnerFileUiState>,
    ) : PropertyOwnerUiState
}
