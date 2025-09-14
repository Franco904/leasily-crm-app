package com.fstengineering.leasilycrm.core.domain.utils

sealed interface DataSourceError : Error {
    enum class DatabaseError : DataSourceError {
        NotFound,
        UnknownError,
    }

    enum class PreferencesError : DataSourceError {
        NotFound,
        SerializationError,
        SecurityError,
        UnknownError,
    }

    enum class RemoteBackendError : DataSourceError {
        BadRequest,
        Unauthorized,
        NotFound,
        ServerError,
        UnknownHttpRemoteError,
        SerializationError,
        Timeout,
        NetworkError,
        UnknownError
    }

    enum class SharedStorageError : DataSourceError {
        NotFound,
        InvalidFile,
        UnknownError,
    }

    fun isRedirectError(): Boolean {
        return this in listOf(
            PreferencesError.NotFound,
            RemoteBackendError.Unauthorized,
            RemoteBackendError.NotFound,
            RemoteBackendError.ServerError,
            RemoteBackendError.UnknownHttpRemoteError,
            RemoteBackendError.UnknownError,
        )
    }
}
