package com.fstengineering.leasilycrm.core.domain.utils

sealed interface AppResultCompound<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : AppResultCompound<D, E>
    data class Errors<out D, out E : RootError>(val errors: List<E>) : AppResultCompound<D, E>
}

inline fun <D, E : RootError> AppResultCompound<D, E>.onSuccess(
    block: (D) -> Unit,
) = apply {
    if (this is AppResultCompound.Success<D, E>) {
        block(data)
    }
}

inline fun <D, E : RootError> AppResultCompound<D, E>.onErrors(
    block: (List<E>) -> Unit,
) = apply {
    if (this is AppResultCompound.Errors<D, E>) {
        block(errors)
    }
}
