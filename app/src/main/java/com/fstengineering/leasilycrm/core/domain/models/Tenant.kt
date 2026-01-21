package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Tenant(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val tag: String? = null,
    val name: String? = null,
    val city: String? = null,
    val identityDocument: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val observations: String? = null,
    val status: TenantStatus = TenantStatus.Lead,
    val isDeleted: Boolean = false,
    val tenantContactOriginType: TenantContactOriginType? = null,
    val assistances: ImmutableList<Assistance> = persistentListOf(),
    val stays: ImmutableList<Stay> = persistentListOf(),
    val userId: Long? = null,
) {
    val isLead = status == TenantStatus.Lead
    val isOld = status == TenantStatus.Old

    val isAnyFormFieldInitialized = tag != null
            || name != null
            || city != null
            || identityDocument != null
            || phone != null
            || email != null
            || tenantContactOriginType != null
            || observations != null
}
