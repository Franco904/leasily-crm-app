package com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.infra.utils.maskFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedStorageDataSource(
    private val appContext: Context,
) {
    /**
     * Nome de exibição do arquivo no [contentUriString]. `null` quando o arquivo não tem nome ou a
     * consulta falha — a falha é registrada em log e não chega ao chamador.
     */
    suspend fun getFileName(
        contentUriString: String,
    ): String? = withContext(Dispatchers.IO) {
        maskFailure(fallback = null) {
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
        }
    }
}
