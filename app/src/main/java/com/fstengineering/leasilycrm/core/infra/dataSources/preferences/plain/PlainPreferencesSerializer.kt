package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain

import androidx.datastore.core.Serializer
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import com.fstengineering.leasilycrm.core.infra.utils.isFatal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PlainPreferencesSerializer : Serializer<PlainPreferences> {
    override val defaultValue: PlainPreferences
        get() = PlainPreferences()

    /** Falha de escrita propaga ao `updateData` do DataStore — nada é gravado pela metade. */
    override suspend fun writeTo(t: PlainPreferences, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.use {
                it.write(Json.encodeToString(t).toByteArray())
            }
        }
    }

    /** Degrada para [defaultValue] quando o arquivo não pode ser lido/desserializado. */
    override suspend fun readFrom(input: InputStream): PlainPreferences {
        return try {
            withContext(Dispatchers.IO) {
                input.use {
                    Json.decodeFromString<PlainPreferences>(it.readBytes().decodeToString())
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: Exception) {
            if (e.isFatal()) throw e
            ExceptionLogger.log(e)
            defaultValue
        }
    }
}
