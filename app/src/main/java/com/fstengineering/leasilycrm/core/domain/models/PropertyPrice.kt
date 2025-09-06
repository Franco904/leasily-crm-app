package com.fstengineering.leasilycrm.core.domain.models

import java.math.BigDecimal
import java.time.LocalDate

data class PropertyPrice(
    val id: Int? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val price: BigDecimal? = null,
    val propertyId: Int? = null,
    val isDeleted: Boolean = false,
)
