package com.fstengineering.leasilycrm.core.domain.helpers

import com.fstengineering.leasilycrm.core.domain.entities.Tenant

sealed interface StayOwnedTenant {
    val tenantId: Long

    data class WithId(override val tenantId: Long) : StayOwnedTenant

    data class WithData(val data: Tenant?) : StayOwnedTenant {
        override val tenantId: Long get() = requireNotNull(data?.id)
    }
}
