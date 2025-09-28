package com.fstengineering.leasilycrm.core.domain.models

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class StayTransfer(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val transferDate: LocalDate? = null,
    val transferAmount: BigDecimal? = null,
    val transferDescription: String? = null,
    val stayId: Long? = null,
    val isDeleted: Boolean = false,
)
