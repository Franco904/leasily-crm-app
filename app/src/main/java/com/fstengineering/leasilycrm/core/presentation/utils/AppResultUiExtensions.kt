package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError

inline fun <D, E : BusinessRuleError> AppResult<D, E>.onApplicationError(
    block: (ApplicationError) -> Unit,
) = apply {
    if (this is AppResult.Error<D, E> && error is ApplicationError) {
        block(error)
    }
}
