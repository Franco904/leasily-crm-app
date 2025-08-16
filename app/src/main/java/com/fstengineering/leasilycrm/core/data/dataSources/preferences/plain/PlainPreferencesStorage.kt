package com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.utils.toPreferencesError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.Flow

class PlainPreferencesStorage(
    private val dataStore: DataStore<PlainPreferences>
) : PreferencesStorage<PlainPreferences> {
    override suspend fun savePreferences(
        onPreferencesFile: (PlainPreferences) -> PlainPreferences,
    ): AppResult<Unit, DataSourceError.PreferencesError> {
        try {
            dataStore.updateData { preferences -> onPreferencesFile(preferences) }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = e.toPreferencesError())
        }
    }

    override suspend fun readPreferences(): AppResult<Flow<PlainPreferences>, DataSourceError.PreferencesError> {
        try {
            return AppResult.Success(data = dataStore.data)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = e.toPreferencesError())
        }
    }

    companion object {
        private const val TAG = "PlainPreferencesStorage"

        val Context.plainDataStore: DataStore<PlainPreferences> by dataStore(
            fileName = "plain-preferences",
            serializer = PlainPreferencesSerializer,
        )
    }
}
