package com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal

data class PropertyPriceReadModel(
    val id: Long?,
    val startDate: AppDate,
    val endDate: AppDate,
    val amount: AppDecimal,
)
