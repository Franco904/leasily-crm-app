package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource<T> {
    suspend fun savePreferences(
        onPreferencesFile: (T) -> T,
    ): AppResult<Unit, InfraError>

    suspend fun readPreferences(): AppResult<Flow<T>, InfraError>
}
