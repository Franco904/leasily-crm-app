package com.fstengineering.leasilycrm.core.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
    val assistances: ImmutableList<Assistance> = persistentListOf(),
    val stays: ImmutableList<Stay> = persistentListOf(),
    val userId: Long? = null,
) {
    fun hasUnfinishedStay() = stays.any { !it.isCheckOutDone }

    fun hasFinishedStays() = stays.any { it.isCheckOutDone }

    fun hasFinishedAssistancesOrStays() = assistances.any { it.isFinished } || hasFinishedStays()
}
