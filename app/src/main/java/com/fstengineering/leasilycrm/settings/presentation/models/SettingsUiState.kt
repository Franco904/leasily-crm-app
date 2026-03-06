package com.fstengineering.leasilycrm.settings.presentation.models

import com.fstengineering.leasilycrm.settings.domain.valueObjects.Settings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Loaded(
        val isTodayCheckInCheckOutNotificationEnabled: Boolean,
    ) : SettingsUiState {
        companion object {
            fun fromValueObject(settings: Settings) = Loaded(
                isTodayCheckInCheckOutNotificationEnabled = settings.isTodayCheckInCheckOutNotificationEnabled,
            )
        }
    }
}