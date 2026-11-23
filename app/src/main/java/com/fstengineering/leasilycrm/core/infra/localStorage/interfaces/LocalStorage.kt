package com.fstengineering.leasilycrm.core.infra.localStorage.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface LocalStorage<T> {
    suspend fun savePreferences(
        onPreferencesFile: (T) -> T,
    ): AppResult<Unit>

    suspend fun readPreferences(): AppResult<Flow<T>>
}
