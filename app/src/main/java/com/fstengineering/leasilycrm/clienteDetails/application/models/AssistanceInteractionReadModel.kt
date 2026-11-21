package com.fstengineering.leasilycrm.clienteDetails.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

data class AssistanceInteractionReadModel(
    val id: Long,
    val date: AppDate,
    val title: String,
    val description: String?,
)
