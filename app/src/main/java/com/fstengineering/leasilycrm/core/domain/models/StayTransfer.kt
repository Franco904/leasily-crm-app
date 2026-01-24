package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

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
