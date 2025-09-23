package com.fstengineering.leasilycrm.core.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Stay(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val isFromOtherRealtor: Boolean = false,
    val qtGuests: Int = 1,
    val totalPrice: BigDecimal? = null,
    val startDatetime: LocalDateTime? = null,
    val endDatetime: LocalDateTime? = null,
    val commission: BigDecimal? = null,
    val observations: String? = null,
    val isFinished: Boolean = false,
    val isDeleted: Boolean = false,
    val property: Property? = null,
)
