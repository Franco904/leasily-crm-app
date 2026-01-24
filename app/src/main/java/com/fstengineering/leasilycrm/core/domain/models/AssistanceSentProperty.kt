package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class AssistanceSentProperty(
    val assistanceId: Long? = null,
    val property: Property? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val isDeleted: Boolean = false,
)
