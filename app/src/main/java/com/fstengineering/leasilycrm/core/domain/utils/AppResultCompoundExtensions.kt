package com.fstengineering.leasilycrm.core.domain.utils

import kotlinx.collections.immutable.ImmutableList

inline fun <D, E : BaseError> AppResultCompound<D, E>.onSuccess(
    block: (D) -> Unit,
) = apply {
    if (this is AppResultCompound.Success<D, E>) {
        block(data)
    }
}

inline fun <D, E : BaseError> AppResultCompound<D, E>.onErrors(
    block: (ImmutableList<E>) -> Unit,
) = apply {
    if (this is AppResultCompound.Errors<D, E>) {
        block(errors)
    }
}

inline fun <D, E : BaseError, R> AppResultCompound<D, E>.fold(
    onErrors: (errors: ImmutableList<E>) -> R,
    onSuccess: (data: D) -> R,
): R {
    return when (this) {
        is AppResultCompound.Errors<D, E> -> onErrors(errors)
        is AppResultCompound.Success<D, E> -> onSuccess(data)
    }
}
