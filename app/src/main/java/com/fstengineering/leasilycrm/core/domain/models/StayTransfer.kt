package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

data class StayTransfer(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val transferDate: AppDate,
    val amount: AppDecimal,
    val description: String?,
    val stayId: Long,
    val isDeleted: Boolean,
)
