package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

inline fun <D, E : BusinessRuleError> AppResultCompound<D, E>.onApplicationError(
    block: (ApplicationError) -> Unit,
) = apply {
    if (this is AppResultCompound.Errors<D, E>) {
        val error = errors
            .filterIsInstance<ApplicationError>()
            .firstOrNull() ?: return@apply

        block(error)
    }
}

inline fun <D, E : BusinessRuleError> AppResultCompound<D, E>.onDomainErrors(
    block: (List<DomainError>) -> Unit,
) = apply {
    if (this is AppResultCompound.Errors<D, E>) {
        val errors = errors
            .filterIsInstance<DomainError>()
            .takeIf { it.isNotEmpty() } ?: return@apply

        block(errors)
    }
}
