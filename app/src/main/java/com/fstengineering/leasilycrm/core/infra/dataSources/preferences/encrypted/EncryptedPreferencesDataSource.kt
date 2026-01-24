package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.Flow

class EncryptedPreferencesDataSource(
    private val dataStore: DataStore<EncryptedPreferences>
) : PreferencesDataSource<EncryptedPreferences> {
    override suspend fun savePreferences(
        onPreferencesFile: (EncryptedPreferences) -> EncryptedPreferences,
    ): AppResult<Unit, DataError> {
        try {
            dataStore.updateData { preferences -> onPreferencesFile(preferences) }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = DataError.UnknownError)
        }
    }

    override suspend fun readPreferences(): AppResult<Flow<EncryptedPreferences>, DataError> {
        try {
            return AppResult.Success(data = dataStore.data)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = DataError.UnknownError)
        }
    }

    companion object {
        val Context.encryptedDataStore: DataStore<EncryptedPreferences> by dataStore(
            fileName = "encrypted-preferences",
            serializer = EncryptedPreferencesSerializer,
        )
    }
}
