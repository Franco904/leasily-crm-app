package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

fun HttpStatusCode.toRemoteDataSourceError() = when (this) {
    HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> ApplicationError.Unauthorized
    HttpStatusCode.RequestTimeout -> ApplicationError.Timeout
    else -> ApplicationError.Unknown
}

fun Exception.toRemoteDataSourceError() = when (this) {
    is ClientRequestException, is ServerResponseException -> response.status.toRemoteDataSourceError()
    is IOException -> ApplicationError.Timeout
    else -> ApplicationError.Unknown
}
