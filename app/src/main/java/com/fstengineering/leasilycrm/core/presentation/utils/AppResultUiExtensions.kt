package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

inline fun <D, E : ApplicationError> AppResult<D, E>.onInfraError(
    block: (ApplicationError.Infra) -> Unit,
) = apply {
    if (this is AppResult.Error<D, E> && error is ApplicationError.Infra) {
        block(error)
    }
}
