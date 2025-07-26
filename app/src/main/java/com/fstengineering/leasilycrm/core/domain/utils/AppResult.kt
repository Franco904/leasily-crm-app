package com.fstengineering.leasilycrm.core.domain.utils

typealias RootError = Error

sealed interface AppResult<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : AppResult<D, E>
    data class Error<out D, out E : RootError>(val error: E) : AppResult<D, E>
}

fun <D, E : RootError> AppResult<D, E>.hasError() = this is AppResult.Error

inline fun <D, E : RootError> AppResult<D, E>.onSuccess(
    block: (D) -> Unit,
) = apply {
    if (this is AppResult.Success<D, E>) {
        block(data)
    }
}

inline fun <D, E : RootError> AppResult<D, E>.onError(
    block: (E) -> Unit,
) = apply {
    if (this is AppResult.Error<D, E>) {
        block(error)
    }
}

inline fun <D, E : RootError, R> AppResult<D, E>.fold(
    onError: (error: E) -> R,
    onSuccess: (data: D) -> R,
): R {
    return when (this) {
        is AppResult.Error<D, E> -> onError(error)
        is AppResult.Success<D, E> -> onSuccess(data)
    }
}
