package com.fstengineering.leasilycrm.core.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Stay(
    val id: Long? = null,
    val isFromOtherRealtor: Boolean = false,
    val qtGuests: Int = 1,
    val totalPrice: BigDecimal? = null,
    val startDatetime: LocalDateTime? = null,
    val endDatetime: LocalDateTime? = null,
    val commission: BigDecimal? = null,
    val observations: String? = null,
    val isDeleted: Boolean = false,
    val property: Property? = null,
)
