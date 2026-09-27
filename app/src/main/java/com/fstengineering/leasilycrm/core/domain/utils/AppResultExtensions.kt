package com.fstengineering.leasilycrm.core.domain.utils

inline fun <D, E> AppResult<D, E>.onSuccess(
    block: (D) -> Unit,
) = apply {
    if (this is AppResult.Success<D, E>) {
        block(data)
    }
}

inline fun <D, E> AppResult<D, E>.onError(
    block: (E) -> Unit,
) = apply {
    if (this is AppResult.Error<D, E>) {
        block(error)
    }
}

inline fun <D, E, R> AppResult<D, E>.fold(
    onError: (error: E) -> R,
    onSuccess: (data: D) -> R,
): R {
    return when (this) {
        is AppResult.Error<D, E> -> onError(error)
        is AppResult.Success<D, E> -> onSuccess(data)
    }
}
