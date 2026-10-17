package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted

import androidx.datastore.core.Serializer
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import com.fstengineering.leasilycrm.core.infra.utils.CryptoUtils
import com.fstengineering.leasilycrm.core.infra.utils.isFatal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object EncryptedPreferencesSerializer : Serializer<EncryptedPreferences> {
    override val defaultValue: EncryptedPreferences
        get() = EncryptedPreferences()

    /** Falha de escrita propaga ao `updateData` do DataStore — nada é gravado pela metade. */
    override suspend fun writeTo(t: EncryptedPreferences, output: OutputStream) {
        val preferencesJson = Json.encodeToString(t)
        val preferencesEncryptedBytes = CryptoUtils.encrypt(preferencesJson.toByteArray())

        withContext(Dispatchers.IO) {
            output.use { it.write(preferencesEncryptedBytes) }
        }
    }

    /**
     * Degrada para [defaultValue] — sem Token de sessão, o Usuário faz novo login — quando a chave
     * do Keystore foi invalidada pela plataforma ou o arquivo não pode ser lido/desserializado.
     * Dado que falha na autenticação da criptografia é adulteração: propaga e crasha.
     */
    override suspend fun readFrom(input: InputStream): EncryptedPreferences {
        return try {
            val preferencesEncryptedBytes = withContext(Dispatchers.IO) {
                input.use { it.readBytes() }
            }

            val preferencesDecryptedBytes = CryptoUtils.decrypt(preferencesEncryptedBytes)

            Json.decodeFromString<EncryptedPreferences>(
                string = preferencesDecryptedBytes.decodeToString(),
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: Exception) {
            if (e.isFatal()) throw e
            ExceptionLogger.log(e)
            defaultValue
        }
    }
}
