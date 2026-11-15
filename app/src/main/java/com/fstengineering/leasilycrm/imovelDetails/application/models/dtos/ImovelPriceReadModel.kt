package com.fstengineering.leasilycrm.imovelDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

data class ImovelPriceReadModel(
    val startDate: AppDate,
    val endDate: AppDate,
    val amount: AppDecimal,
)
