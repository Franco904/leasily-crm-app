package com.fstengineering.leasilycrm.core.data.exceptions

import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.UnknownHostException

sealed class RemoteBackendException(message: String) : Exception(message) {
    // HTTP errors
    class BadRequest : RemoteBackendException("Invalid request body or headers.")
    class Unauthorized : RemoteBackendException("Invalid credentials or session expired.")
    class NotFound : RemoteBackendException("Requested resource not found.")
    class ServerError(statusCode: String) :
        RemoteBackendException("Server error: $statusCode code. Please try again later.")

    class UnknownRemoteBackendRemoteError(statusCode: String) :
        RemoteBackendException("Unexpected response: $statusCode code.")

    // Other errors
    class SerializationError(message: String?) :
        RemoteBackendException("Request or response data parsing failed, unexpected response format. $message.")

    class Timeout(message: String?) : RemoteBackendException("Request timed out: $message")
    class NetworkError(message: String?) :
        RemoteBackendException("Network/Internet connection failed. Message: $message")

    class UnknownError(message: String?) : RemoteBackendException("Unknown error: $message.")

    fun toRemoteBackendError() = when (this) {
        is BadRequest -> DataSourceError.RemoteBackendError.BadRequest
        is Unauthorized -> DataSourceError.RemoteBackendError.Unauthorized
        is NotFound -> DataSourceError.RemoteBackendError.NotFound
        is ServerError -> DataSourceError.RemoteBackendError.ServerError
        is UnknownRemoteBackendRemoteError -> DataSourceError.RemoteBackendError.UnknownHttpRemoteError
        is SerializationError -> DataSourceError.RemoteBackendError.SerializationError
        is Timeout -> DataSourceError.RemoteBackendError.Timeout
        is NetworkError -> DataSourceError.RemoteBackendError.NetworkError
        is UnknownError -> DataSourceError.RemoteBackendError.UnknownError
    }
}

fun HttpStatusCode.asRemoteBackendException(): RemoteBackendException {
    return when (this) {
        HttpStatusCode.BadRequest -> RemoteBackendException.BadRequest()
        HttpStatusCode.Unauthorized -> RemoteBackendException.Unauthorized()
        HttpStatusCode.NotFound -> RemoteBackendException.NotFound()
        else -> {
            if (value in 500..599) {
                RemoteBackendException.ServerError(statusCode = "$value")
            } else {
                RemoteBackendException.UnknownRemoteBackendRemoteError(statusCode = "$value")
            }
        }
    }
}

fun Exception.asRemoteBackendException(): RemoteBackendException {
    val remoteBackendException = when (this) {
        is SerializationException -> {
            RemoteBackendException.SerializationError(message = "$cause: ${message ?: "Unknown serialization error"}")
        }

        is HttpRequestTimeoutException -> {
            RemoteBackendException.Timeout(message = "$cause: ${message ?: "Unknown time out error"}")
        }

        is ConnectException -> {
            RemoteBackendException.NetworkError(message = "$cause: ${message ?: "Unknown connection error"}")
        }

        is UnknownHostException -> {
            RemoteBackendException.NetworkError(message = "$cause: ${message ?: "Unknown unknown host error"}")
        }

        is IOException -> {
            RemoteBackendException.NetworkError(message = "$cause: ${message ?: "Unknown I/O error"}")
        }

        else -> {
            RemoteBackendException.UnknownError(message = "$cause: ${message ?: "Unknown remote backend error"}")
        }
    }

    return remoteBackendException.apply {
        initCause(this@asRemoteBackendException)
    }
}
