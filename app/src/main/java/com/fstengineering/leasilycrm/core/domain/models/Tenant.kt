package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import kotlinx.collections.immutable.ImmutableList

data class Tenant(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val tag: String?,
    val name: String,
    val city: String?,
    val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val tenantContactOriginType: TenantContactOriginType?,
    val observations: String?,
    val status: TenantStatus,
    val isDeleted: Boolean,
    val assistances: ImmutableList<Assistance>,
    val stays: ImmutableList<Stay>,
    val userId: Long,
) {
    val isLead get() = status == TenantStatus.Lead
    val isOld get() = status == TenantStatus.Old

    val isAnyFormFieldInitialized
        get() = tag != null
                || name != null
                || city != null
                || identityDocument != null
                || phone != null
                || email != null
                || tenantContactOriginType != null
                || observations != null
}
