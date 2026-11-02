package com.fstengineering.leasilycrm.imovelRegistration.application.models.commands

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

data class ImovelPriceCommandModel(
    val id: Long?,
    val startDate: AppDate?,
    val endDate: AppDate?,
    val amount: AppDecimal?,
)
