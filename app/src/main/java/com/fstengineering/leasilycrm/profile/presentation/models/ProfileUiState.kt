package com.fstengineering.leasilycrm.profile.presentation.models

import com.fstengineering.leasilycrm.core.domain.entities.User

sealed interface ProfileUiState {
    data object Empty : ProfileUiState

    data class Loaded(
        val name: String,
        val creciNumber: String?,
        val profilePictureUrl: String,
    ) : ProfileUiState {
        companion object {
            fun fromDomain(user: User): Loaded {
                return Loaded(
                    name = user.name,
                    creciNumber = user.creci?.value,
                    profilePictureUrl = user.profilePictureUrl,
                )
            }
        }
    }
}
