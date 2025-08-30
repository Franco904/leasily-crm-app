package com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import com.fstengineering.leasilycrm.core.data.utils.toSharedStorageError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import io.ktor.utils.io.CancellationException

class SharedStorageImpl(
    private val appContext: Context,
) : SharedStorage {
    override suspend fun getFileName(contentUriString: String): AppResult<String, DataSourceError> {
        try {
            val query = appContext.contentResolver.query(
                /* uri = */ contentUriString.toUri(),
                /* projection = */ arrayOf(OpenableColumns.DISPLAY_NAME),
                /* selection = */ null,
                /* selectionArgs = */ null,
                /* sortOrder = */ null,
            )

            val name = query?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }

            return when {
                name == null -> AppResult.Error(error = DataSourceError.SharedStorageError.NotFound)
                else -> AppResult.Success(data = name)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = e.toSharedStorageError())
        }
    }
}
