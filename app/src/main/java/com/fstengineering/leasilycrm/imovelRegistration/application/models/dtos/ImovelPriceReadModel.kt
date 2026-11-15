package com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

data class ImovelPriceReadModel(
    val id: Long?,
    val startDate: AppDate,
    val endDate: AppDate,
    val amount: AppDecimal,
)
