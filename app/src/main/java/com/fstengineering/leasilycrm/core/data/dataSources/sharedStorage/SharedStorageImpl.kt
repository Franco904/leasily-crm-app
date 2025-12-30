package com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedStorageImpl(
    private val appContext: Context,
) : SharedStorage {
    override suspend fun getFileName(
        contentUriString: String,
    ): String = withContext(Dispatchers.IO) {
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

            name ?: appContext.getString(R.string.unknown_text).lowercase()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            appContext.getString(R.string.unknown_text).lowercase()
        }
    }
}
