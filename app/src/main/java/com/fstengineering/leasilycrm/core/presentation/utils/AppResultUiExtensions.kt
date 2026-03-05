package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.SystemError

inline fun <D, E : SystemError> AppResult<D, E>.onExternalError(
    block: (ApplicationError.External) -> Unit,
) = apply {
    if (this is AppResult.Error<D, E> && error is ApplicationError.External) {
        block(error)
    }
}
