package com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.fstengineering.leasilycrm.BuildConfig
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedStorageDataSource(
    private val appContext: Context,
) {
    suspend fun getFileName(
        contentUriString: String,
    ): String? = withContext(Dispatchers.IO) {
        try {
            val query = appContext.contentResolver.query(
                /* uri = */ contentUriString.toUri(),
                /* projection = */ arrayOf(OpenableColumns.DISPLAY_NAME),
                /* selection = */ null,
                /* selectionArgs = */ null,
                /* sortOrder = */ null,
            )

            val name = query?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }

            name
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            null
        }
    }
}
