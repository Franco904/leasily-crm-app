package com.fstengineering.leasilycrm.core.domain.utils

inline fun <D> AppResult<D>.onSuccess(
    block: (D) -> Unit,
) = apply {
    if (this is AppResult.Success<D>) {
        block(data)
    }
}

inline fun <D> AppResult<D>.onError(
    block: (BaseError) -> Unit,
) = apply {
    if (this is AppResult.Error) {
        block(error)
    }
}

inline fun <D, R> AppResult<D>.fold(
    onError: (error: BaseError) -> R,
    onSuccess: (data: D) -> R,
): R {
    return when (this) {
        is AppResult.Error -> onError(error)
        is AppResult.Success<D> -> onSuccess(data)
    }
}
