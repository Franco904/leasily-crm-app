package com.fstengineering.leasilycrm.core.domain.models

sealed interface StayOwnedTenant {
    val tenantId: Long

    data class WithId(override val tenantId: Long) : StayOwnedTenant

    data class WithData(val data: Tenant) : StayOwnedTenant {
        override val tenantId: Long get() = requireNotNull(data.id)
    }
}
