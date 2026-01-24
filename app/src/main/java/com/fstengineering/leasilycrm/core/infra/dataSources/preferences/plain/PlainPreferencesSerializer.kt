package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PlainPreferencesSerializer : Serializer<PlainPreferences> {
    private const val TAG = "PlainPreferencesSerializer"

    override val defaultValue: PlainPreferences
        get() = PlainPreferences()

    override suspend fun writeTo(t: PlainPreferences, output: OutputStream) {
        try {
            withContext(Dispatchers.IO) {
                output.use {
                    it.write(Json.encodeToString(t).toByteArray())
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Log.e(TAG, e.toString())
        }
    }

    override suspend fun readFrom(input: InputStream): PlainPreferences {
        return try {
            withContext(Dispatchers.IO) {
                input.use {
                    Json.decodeFromString<PlainPreferences>(it.readBytes().decodeToString())
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Log.e(TAG, e.toString())
            defaultValue
        }
    }
}
