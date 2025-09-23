package com.fstengineering.leasilycrm.core.domain.models

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class PropertyPrice(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val price: BigDecimal? = null,
    val propertyId: Long? = null,
    val isDeleted: Boolean = false,
)
