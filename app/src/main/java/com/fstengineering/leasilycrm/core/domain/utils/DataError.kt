package com.fstengineering.leasilycrm.core.domain.utils

sealed interface DataError : Error {
    data object NoInternetError : DataError
    data object TimeOutError : DataError
    data object UnauthorizedError : DataError
    data object UnknownError : DataError
}
