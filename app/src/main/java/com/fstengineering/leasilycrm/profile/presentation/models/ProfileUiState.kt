package com.fstengineering.leasilycrm.profile.presentation.models

import com.fstengineering.leasilycrm.profile.application.models.UserReadModel

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    data class Loaded(
        val name: String,
        val creciNumber: String?,
        val profilePictureUrl: String,
    ) : ProfileUiState {
        companion object {
            fun fromReadModel(readModel: UserReadModel): Loaded {
                return Loaded(
                    name = readModel.name,
                    creciNumber = readModel.creci,
                    profilePictureUrl = readModel.profilePictureUrl,
                )
            }
        }
    }
}
