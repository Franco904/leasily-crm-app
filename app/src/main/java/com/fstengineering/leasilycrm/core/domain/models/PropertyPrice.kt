package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

data class PropertyPrice(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val startDate: AppDate? = null,
    val endDate: AppDate? = null,
    val price: AppDecimal? = null,
    val propertyId: Long? = null,
    val isDeleted: Boolean = false,
)
