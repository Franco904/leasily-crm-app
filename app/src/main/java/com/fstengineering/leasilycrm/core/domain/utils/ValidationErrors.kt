package com.fstengineering.leasilycrm.core.domain.utils

import kotlinx.collections.immutable.ImmutableList

data class ValidationErrors(val errors: ImmutableList<DomainError>) : BaseError
