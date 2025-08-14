package com.fstengineering.leasilycrm.core.data.exceptions

import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.UnknownHostException

fun HttpStatusCode.toRemoteBackendError() = when (this) {
    HttpStatusCode.BadRequest -> DataSourceError.RemoteBackendError.BadRequest
    HttpStatusCode.Unauthorized -> DataSourceError.RemoteBackendError.Unauthorized
    HttpStatusCode.NotFound -> DataSourceError.RemoteBackendError.NotFound
    else -> {
        if (value in 500..599) {
            DataSourceError.RemoteBackendError.ServerError
        } else {
            DataSourceError.RemoteBackendError.UnknownHttpRemoteError
        }
    }
}

fun Exception.toRemoteBackendError() = when (this) {
    is SerializationException -> DataSourceError.RemoteBackendError.SerializationError
    is HttpRequestTimeoutException -> DataSourceError.RemoteBackendError.Timeout
    is ConnectException -> DataSourceError.RemoteBackendError.NetworkError
    is UnknownHostException -> DataSourceError.RemoteBackendError.NetworkError
    is IOException -> DataSourceError.RemoteBackendError.NetworkError
    else -> DataSourceError.RemoteBackendError.UnknownError
}
