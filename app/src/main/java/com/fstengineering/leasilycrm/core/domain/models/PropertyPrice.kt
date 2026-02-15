package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

data class PropertyPrice(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val startDate: AppDate,
    val endDate: AppDate,
    val price: AppDecimal,
    val propertyId: Long,
    val isDeleted: Boolean,
)
