package com.fstengineering.leasilycrm.core.data.utils

import com.fstengineering.leasilycrm.core.domain.utils.DataError
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

fun HttpStatusCode.toRemoteBackendDataError() = when (this) {
    HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> DataError.UnauthorizedError
    HttpStatusCode.RequestTimeout -> DataError.TimeOutError
    else -> DataError.UnknownError
}

fun Exception.toRemoteBackendDataError() = when (this) {
    is IOException -> DataError.TimeOutError
    else -> DataError.UnknownError
}
