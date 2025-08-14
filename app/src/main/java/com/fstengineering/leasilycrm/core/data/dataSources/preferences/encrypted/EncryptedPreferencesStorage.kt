package com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.exceptions.toPreferencesError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.Flow

class EncryptedPreferencesStorage(
    private val dataStore: DataStore<EncryptedPreferences>
) : PreferencesStorage<EncryptedPreferences> {
    override suspend fun savePreferences(
        onPreferencesFile: (EncryptedPreferences) -> EncryptedPreferences,
    ): AppResult<Unit, DataSourceError.PreferencesError> {
        try {
            dataStore.updateData { preferences -> onPreferencesFile(preferences) }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "savePreferences error - ${e.message}")
                e.printStackTrace()
            }

            return AppResult.Error(error = e.toPreferencesError())
        }
    }

    override suspend fun readPreferences(): AppResult<Flow<EncryptedPreferences>, DataSourceError.PreferencesError> {
        try {
            return AppResult.Success(data = dataStore.data)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "readPreferences error - ${e.message}")
                e.printStackTrace()
            }

            return AppResult.Error(error = e.toPreferencesError())
        }
    }

    companion object {
        private const val TAG = "EncryptedPreferencesStorage"

        val Context.encryptedDataStore: DataStore<EncryptedPreferences> by dataStore(
            fileName = "encrypted-preferences",
            serializer = EncryptedPreferencesSerializer,
        )
    }
}
