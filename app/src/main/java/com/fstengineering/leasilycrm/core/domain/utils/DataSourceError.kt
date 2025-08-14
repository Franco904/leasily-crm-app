package com.fstengineering.leasilycrm.core.domain.utils

sealed interface DataSourceError : Error {
    enum class PreferencesError : DataSourceError {
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

    fun isRedirectError(): Boolean {
        return this in listOf(
            RemoteBackendError.Unauthorized,
            RemoteBackendError.NotFound,
            RemoteBackendError.ServerError,
            RemoteBackendError.UnknownHttpRemoteError,
            RemoteBackendError.UnknownError,
        )
    }
}
