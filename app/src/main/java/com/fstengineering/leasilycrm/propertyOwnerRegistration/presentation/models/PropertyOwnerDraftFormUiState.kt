package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

sealed interface PropertyOwnerDraftFormUiState {
    data object Loading : PropertyOwnerDraftFormUiState

    data class Loaded(
        val id: Long?,
        val createDateTime: AppDateTime?,
        val name: String?,
        val phone: String?,
        val email: String?,
        val paymentKey: String?,
        val files: ImmutableList<PropertyOwnerFileUiState>,
    ) : PropertyOwnerDraftFormUiState {
        val isAnyFormFieldInitialized: Boolean
            get() = name != null || phone != null || email != null || paymentKey != null

        fun addFile(newFile: PropertyOwnerFileUiState) = copy(
            files = (files + newFile).toPersistentList(),
        )
    }
}
