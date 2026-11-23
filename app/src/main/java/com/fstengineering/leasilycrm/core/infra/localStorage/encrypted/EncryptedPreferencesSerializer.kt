package com.fstengineering.leasilycrm.core.infra.localStorage.encrypted

import androidx.datastore.core.Serializer
import com.fstengineering.leasilycrm.core.infra.utils.CryptoUtils
import com.fstengineering.leasilycrm.core.infra.utils.onSerializationRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object EncryptedPreferencesSerializer : Serializer<EncryptedPreferences> {
    override val defaultValue: EncryptedPreferences
        get() = EncryptedPreferences()

    /** Falha de escrita propaga ao `updateData` do DataStore — nada é gravado pela metade. */
    override suspend fun writeTo(preferences: EncryptedPreferences, output: OutputStream) {
        val preferencesJson = Json.encodeToString(preferences)
        val preferencesEncryptedBytes = CryptoUtils.encrypt(preferencesJson.toByteArray())

        withContext(Dispatchers.IO) {
            output.use { it.write(preferencesEncryptedBytes) }
        }
    }

    /**
     * Quando a chave do Keystore foi invalidada pela plataforma ou o arquivo não pode ser
     * lido/desserializado, lança `CorruptionException`; o `corruptionHandler` do DataStore o
     * substitui por [defaultValue] — sem Token de sessão, o Usuário faz novo login.
     * Dado que falha na autenticação da criptografia é adulteração: propaga e crasha.
     */
    override suspend fun readFrom(input: InputStream): EncryptedPreferences {
        return onSerializationRead {
            val preferencesEncryptedBytes = withContext(Dispatchers.IO) {
                input.use { it.readBytes() }
            }

            val preferencesDecryptedBytes = CryptoUtils.decrypt(preferencesEncryptedBytes)

            Json.decodeFromString<EncryptedPreferences>(
                string = preferencesDecryptedBytes.decodeToString(),
            )
        }
    }
}
