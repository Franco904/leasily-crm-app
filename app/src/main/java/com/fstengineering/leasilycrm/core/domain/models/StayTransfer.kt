package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.application.models.AppDecimal

data class StayTransfer(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val transferDate: AppDate? = null,
    val transferAmount: AppDecimal? = null,
    val transferDescription: String? = null,
    val stayId: Long? = null,
    val isDeleted: Boolean = false,
)
