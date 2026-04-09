package com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate

data class AssistanceInteractionReadModel(
    val id: Long?,
    val date: AppDate,
    val title: String,
    val description: String?,
)
