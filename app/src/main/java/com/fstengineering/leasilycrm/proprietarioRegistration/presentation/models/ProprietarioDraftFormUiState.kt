package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

sealed interface ProprietarioDraftFormUiState {
    data object Loading : ProprietarioDraftFormUiState

    data class Loaded(
        val id: Long? = null,
        val createDateTime: AppDateTime? = null,
        val name: String? = null,
        val phone: String? = null,
        val email: String? = null,
        val paymentKey: String? = null,
        val files: ImmutableList<ProprietarioFileUiState> = persistentListOf(),
    ) : ProprietarioDraftFormUiState {
        val isAnyFormFieldInitialized: Boolean
            get() = name != null || phone != null || email != null || paymentKey != null

        fun addFile(newFile: ProprietarioFileUiState) = copy(
            files = (files + newFile).toPersistentList(),
        )

        companion object {
            val Empty = Loaded()
        }
    }
}
