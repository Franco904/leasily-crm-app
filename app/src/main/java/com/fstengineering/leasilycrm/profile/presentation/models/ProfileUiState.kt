package com.fstengineering.leasilycrm.profile.presentation.models

import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.presentation.utils.models.FieldValueState
import com.fstengineering.leasilycrm.core.presentation.utils.models.FieldValueState.Applicable

sealed interface ProfileUiState {
    data object Empty : ProfileUiState

    data class Loaded(
        val name: FieldValueState<String>,
        val creciNumber: FieldValueState<String>,
        val profilePictureUrl: FieldValueState<String>,
    ) : ProfileUiState {
        companion object {
            fun fromDomain(user: User): Loaded {
                return Loaded(
                    name = Applicable(user.name.value),
                    creciNumber = Applicable(user.creci?.value),
                    profilePictureUrl = Applicable(user.profilePictureUrl),
                )
            }
        }
    }
}
