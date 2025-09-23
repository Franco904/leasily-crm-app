package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class Tenant(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val name: String? = null,
    val city: String? = null,
    val identityDocument: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val observations: String? = null,
    val status: Int = 0,
    val isDeleted: Boolean = false,
    val customerSegment: CustomerSegment? = null,
    val tenantContactOriginType: TenantContactOriginType? = null,
    val userId: Long? = null,
)
