package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class AssistanceSentProperty(
    val assistanceId: Long,
    val property: Property,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val isDeleted: Boolean,
)
