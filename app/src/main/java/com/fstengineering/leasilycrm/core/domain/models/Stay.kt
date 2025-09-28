package com.fstengineering.leasilycrm.core.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal
import java.time.LocalDateTime

data class Stay(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val isFromOtherRealtor: Boolean = false,
    val qtGuests: Int = 1,
    val totalPrice: BigDecimal? = null,
    val checkInDateTime: LocalDateTime? = null,
    val checkInHourMinute: Pair<Int?, Int?> = null to null,
    val checkOutDateTime: LocalDateTime? = null,
    val checkOutHourMinute: Pair<Int?, Int?> = null to null,
    val commission: BigDecimal? = null,
    val observations: String? = null,
    val isFinished: Boolean = false,
    val isDeleted: Boolean = false,
    val property: Property? = null,
    val tenant: Tenant? = null,
    val transfers: ImmutableList<StayTransfer> = persistentListOf(),
    val files: ImmutableList<StayFile> = persistentListOf(),
    val userId: Long? = null,
)
