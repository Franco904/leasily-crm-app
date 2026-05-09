package com.fstengineering.leasilycrm.settings.models

import com.fstengineering.leasilycrm.core.application.models.SettingsSharedReadModel

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Loaded(
        val isTodayCheckInCheckOutNotificationEnabled: Boolean,
    ) : SettingsUiState {
        companion object {
            fun fromReadModel(readModel: SettingsSharedReadModel) = Loaded(
                isTodayCheckInCheckOutNotificationEnabled = readModel.isTodayCheckInCheckOutNotificationEnabled,
            )
        }
    }
}