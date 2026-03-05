package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.ImmutableList

interface MonitoringAdapter {
    fun logDomainErrors(tag: String, errors: ImmutableList<DomainError>)
}
