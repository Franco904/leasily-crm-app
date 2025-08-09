package com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.coroutines.flow.Flow

interface PreferencesStorage<T> {
    suspend fun savePreferences(
        onPreferencesFile: (T) -> T,
    ): AppResult<Unit, DataSourceError.PreferencesError>

    suspend fun readPreferences(): AppResult<Flow<T>, DataSourceError.PreferencesError>
}
