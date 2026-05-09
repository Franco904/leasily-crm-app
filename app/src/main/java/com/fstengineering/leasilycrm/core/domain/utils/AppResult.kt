package com.fstengineering.leasilycrm.core.domain.utils

sealed interface AppResult<out D, out E : BaseError> {
    data class Success<out D, out E : BaseError>(val data: D) : AppResult<D, E>
    data class Error<out D, out E : BaseError>(val error: E) : AppResult<D, E>
}
