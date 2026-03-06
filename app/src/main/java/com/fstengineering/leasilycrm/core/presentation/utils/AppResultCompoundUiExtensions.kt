package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound

inline fun <D, E : ApplicationError> AppResultCompound<D, E>.onInfraError(
    block: (ApplicationError.Infra) -> Unit,
) = apply {
    if (this is AppResultCompound.Errors<D, E> && errors.any { it is ApplicationError.Infra }) {
        val error = errors
            .filterIsInstance<ApplicationError.Infra>()
            .firstOrNull() ?: return@apply

        block(error)
    }
}

inline fun <D, E : ApplicationError> AppResultCompound<D, E>.onNonInfraErrors(
    block: (List<E>) -> Unit,
) = apply {
    if (this is AppResultCompound.Errors<D, E>) {
        val errors = errors
            .filter { it !is ApplicationError.Infra }
            .takeIf { it.isNotEmpty() } ?: return@apply

        block(errors)
    }
}
