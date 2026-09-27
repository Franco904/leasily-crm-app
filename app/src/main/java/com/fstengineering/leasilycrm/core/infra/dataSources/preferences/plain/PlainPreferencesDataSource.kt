package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.Flow

class PlainPreferencesDataSource(
    private val dataStore: DataStore<PlainPreferences>
) : PreferencesDataSource<PlainPreferences> {
    override suspend fun savePreferences(
        onPreferencesFile: (PlainPreferences) -> PlainPreferences,
    ): AppResult<Unit, ApplicationError> {
        try {
            dataStore.updateData { preferences -> onPreferencesFile(preferences) }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = ApplicationError.Unknown)
        }
    }

    override suspend fun readPreferences(): AppResult<Flow<PlainPreferences>, ApplicationError> {
        try {
            return AppResult.Success(data = dataStore.data)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = ApplicationError.Unknown)
        }
    }

    companion object {
        val Context.plainDataStore: DataStore<PlainPreferences> by dataStore(
            fileName = "plain-preferences",
            serializer = PlainPreferencesSerializer,
        )
    }
}
