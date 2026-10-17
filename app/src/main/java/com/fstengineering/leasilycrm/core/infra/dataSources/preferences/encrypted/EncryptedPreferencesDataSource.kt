package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.utils.isFatal
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.Flow

class EncryptedPreferencesDataSource(
    private val dataStore: DataStore<EncryptedPreferences>
) : PreferencesDataSource<EncryptedPreferences> {
    override suspend fun savePreferences(
        onPreferencesFile: (EncryptedPreferences) -> EncryptedPreferences,
    ): AppResult<Unit, ApplicationError> {
        try {
            dataStore.updateData { preferences -> onPreferencesFile(preferences) }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException || e.isFatal()) throw e
            ExceptionLogger.log(e)

            return AppResult.Error(error = ApplicationError.Unknown)
        }
    }

    override suspend fun readPreferences(): AppResult<Flow<EncryptedPreferences>, ApplicationError> {
        try {
            return AppResult.Success(data = dataStore.data)
        } catch (e: Exception) {
            if (e is CancellationException || e.isFatal()) throw e
            ExceptionLogger.log(e)

            return AppResult.Error(error = ApplicationError.Unknown)
        }
    }

    companion object {
        val Context.encryptedDataStore: DataStore<EncryptedPreferences> by dataStore(
            fileName = "encrypted-preferences",
            serializer = EncryptedPreferencesSerializer,
        )
    }
}
