package com.fstengineering.leasilycrm.core.infra.utils

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

fun HttpStatusCode.toRemoteDataSourceError() = when (this) {
    HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> InfraError.Unauthorized
    HttpStatusCode.RequestTimeout -> InfraError.Timeout
    else -> InfraError.Unknown
}

fun Exception.toRemoteDataSourceError() = when (this) {
    is ClientRequestException, is ServerResponseException -> response.status.toRemoteDataSourceError()
    is IOException -> InfraError.Timeout
    else -> InfraError.Unknown
}
