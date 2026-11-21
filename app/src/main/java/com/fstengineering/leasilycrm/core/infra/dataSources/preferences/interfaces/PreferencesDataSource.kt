package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource<T> {
    suspend fun savePreferences(
        onPreferencesFile: (T) -> T,
    ): AppResult<Unit>

    suspend fun readPreferences(): AppResult<Flow<T>>
}
