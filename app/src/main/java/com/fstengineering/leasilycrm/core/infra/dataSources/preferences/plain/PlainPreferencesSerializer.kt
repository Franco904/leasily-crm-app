package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain

import androidx.datastore.core.Serializer
import com.fstengineering.leasilycrm.core.infra.utils.onSerializationRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PlainPreferencesSerializer : Serializer<PlainPreferences> {
    override val defaultValue: PlainPreferences
        get() = PlainPreferences()

    /** Falha de escrita propaga ao `updateData` do DataStore — nada é gravado pela metade. */
    override suspend fun writeTo(preferences: PlainPreferences, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.use {
                it.write(Json.encodeToString(preferences).toByteArray())
            }
        }
    }

    /**
     * Arquivo que não pode ser lido/desserializado vira `CorruptionException`; o `corruptionHandler`
     * do DataStore o substitui por [defaultValue].
     */
    override suspend fun readFrom(input: InputStream): PlainPreferences {
        return onSerializationRead {
            withContext(Dispatchers.IO) {
                input.use {
                    Json.decodeFromString<PlainPreferences>(it.readBytes().decodeToString())
                }
            }
        }
    }
}
