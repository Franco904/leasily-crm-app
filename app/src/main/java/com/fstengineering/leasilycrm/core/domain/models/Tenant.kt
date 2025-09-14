package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDate

data class Tenant(
    val id: Long? = null,
    val registerDate: LocalDate? = null,
    val name: String? = null,
    val city: String? = null,
    val identityDocument: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val observations: String? = null,
    val isLead: Boolean = true,
    val isDeleted: Boolean = false,
    val customerSegment: CustomerSegment? = null,
    val tenantContactOriginType: TenantContactOriginType? = null,
    val userId: Long? = null,
)
