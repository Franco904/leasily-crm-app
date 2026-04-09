package com.fstengineering.leasilycrm.propertyDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal

data class PropertyPriceReadModel(
    val startDate: AppDate,
    val endDate: AppDate,
    val amount: AppDecimal,
)
