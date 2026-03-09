package com.fstengineering.leasilycrm.settings.models

import com.fstengineering.leasilycrm.settings.application.models.SettingsReadModel

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Loaded(
        val isTodayCheckInCheckOutNotificationEnabled: Boolean,
    ) : SettingsUiState {
        companion object {
            fun fromReadModel(readModel: SettingsReadModel) = Loaded(
                isTodayCheckInCheckOutNotificationEnabled = readModel.isTodayCheckInCheckOutNotificationEnabled,
            )
        }
    }
}