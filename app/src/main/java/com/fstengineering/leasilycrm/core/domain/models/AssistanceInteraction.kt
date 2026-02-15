package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class AssistanceInteraction(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val interactionDate: AppDate,
    val title: String,
    val description: String?,
    val assistanceId: Long,
    val isDeleted: Boolean,
)
