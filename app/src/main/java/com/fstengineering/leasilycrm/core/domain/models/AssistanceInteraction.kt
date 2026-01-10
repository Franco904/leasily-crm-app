package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.application.models.AppDateTime

data class AssistanceInteraction(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val interactionDate: AppDate? = null,
    val title: String? = null,
    val description: String? = null,
    val assistanceId: Long? = null,
    val isDeleted: Boolean = false,
)
