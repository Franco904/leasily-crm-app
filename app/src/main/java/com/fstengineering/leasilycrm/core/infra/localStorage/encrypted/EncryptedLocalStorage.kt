package com.fstengineering.leasilycrm.core.infra.localStorage.encrypted

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.utils.onBoundary
import kotlinx.coroutines.flow.Flow

class EncryptedLocalStorage(
    private val dataStore: DataStore<EncryptedPreferences>
) : LocalStorage<EncryptedPreferences> {
    override suspend fun savePreferences(
        onPreferencesFile: (EncryptedPreferences) -> EncryptedPreferences,
    ): AppResult<Unit> {
        return onBoundary {
            dataStore.updateData { preferences -> onPreferencesFile(preferences) }

            Unit
        }
    }

    override suspend fun readPreferences(): AppResult<Flow<EncryptedPreferences>> {
        return AppResult.Success(data = dataStore.data)
    }

    companion object {
        val Context.encryptedDataStore: DataStore<EncryptedPreferences> by dataStore(
            fileName = "encrypted-preferences",
            serializer = EncryptedPreferencesSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { EncryptedPreferences() },
        )
    }
}
