package com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface PreferencesStorage<T> {
    suspend fun savePreferences(
        onPreferencesFile: (T) -> T,
    ): AppResult<Unit, DataError>

    suspend fun readPreferences(): AppResult<Flow<T>, DataError>
}
