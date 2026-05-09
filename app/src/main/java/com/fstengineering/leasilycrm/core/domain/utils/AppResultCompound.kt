package com.fstengineering.leasilycrm.core.domain.utils

import kotlinx.collections.immutable.ImmutableList

sealed interface AppResultCompound<out D, out E : BaseError> {
    data class Success<out D, out E : BaseError>(val data: D) : AppResultCompound<D, E>
    data class Errors<out D, out E : BaseError>(val errors: ImmutableList<E>) :
        AppResultCompound<D, E>
}
