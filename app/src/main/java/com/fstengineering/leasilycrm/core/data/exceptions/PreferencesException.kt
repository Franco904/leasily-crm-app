package com.fstengineering.leasilycrm.core.data.exceptions

import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.serialization.SerializationException
import java.io.IOException

sealed class PreferencesException(message: String) : Exception(message) {
    class IOError(message: String?) :
        PreferencesException("Failed to read/write to preferences file: $message.")

    class SerializationError(message: String?) :
        PreferencesException("Preferences data parsing failed, unexpected format: $message.")

    class CryptoException(message: String?) :
        PreferencesException("Crypto failed: $message.")

    class UnknownError(message: String?) : PreferencesException("Unknown error: $message.")

    fun toPreferencesError() = when (this) {
        is IOError -> DataSourceError.PreferencesError.SerializationError
        is SerializationError -> DataSourceError.PreferencesError.SerializationError
        is CryptoException -> DataSourceError.PreferencesError.UnknownError
        is UnknownError -> DataSourceError.PreferencesError.UnknownError
    }
}

fun Exception.toPreferencesException(): PreferencesException {
    val preferencesException = when (this) {
        is IOException -> {
            PreferencesException.IOError(message = "$cause: ${message ?: "Unknown I/O error"}")
        }

        is SerializationException -> {
            PreferencesException.SerializationError(message = "$cause: ${message ?: "Unknown serialization error"}")
        }

        is CryptoException -> {
            PreferencesException.CryptoException(message = "$cause: ${message ?: "Unknown crypto utils error"}")
        }

        else -> {
            PreferencesException.UnknownError(message = "$cause: ${message ?: "Unknown preferences error"}")
        }
    }

    return preferencesException.apply {
        initCause(this@toPreferencesException)
    }
}
