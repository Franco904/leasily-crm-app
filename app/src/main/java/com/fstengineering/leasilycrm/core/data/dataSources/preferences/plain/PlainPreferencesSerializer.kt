package com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain

import android.util.Log
import androidx.datastore.core.Serializer
import com.fstengineering.leasilycrm.core.data.exceptions.toPreferencesException
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
            Log.e(TAG, "${e.toPreferencesException().message}")
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
            Log.e(TAG, "${e.toPreferencesException().message}")
            defaultValue
        }
    }
}
