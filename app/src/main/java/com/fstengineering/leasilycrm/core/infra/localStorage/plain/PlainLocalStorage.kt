package com.fstengineering.leasilycrm.core.infra.localStorage.plain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.utils.onBoundary
import kotlinx.coroutines.flow.Flow

class PlainLocalStorage(
    private val dataStore: DataStore<PlainPreferences>
) : LocalStorage<PlainPreferences> {
    override suspend fun savePreferences(
        onPreferencesFile: (PlainPreferences) -> PlainPreferences,
    ): AppResult<Unit> {
        return onBoundary {
            dataStore.updateData { preferences -> onPreferencesFile(preferences) }

            Unit
        }
    }

    override suspend fun readPreferences(): AppResult<Flow<PlainPreferences>> {
        return AppResult.Success(data = dataStore.data)
    }

    companion object {
        val Context.plainDataStore: DataStore<PlainPreferences> by dataStore(
            fileName = "plain-preferences",
            serializer = PlainPreferencesSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { PlainPreferences() },
        )
    }
}
