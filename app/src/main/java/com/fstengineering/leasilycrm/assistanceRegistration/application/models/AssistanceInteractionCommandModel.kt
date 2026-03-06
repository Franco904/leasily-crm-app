package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate

data class AssistanceInteractionCommandModel(
    val id: Long?,
    val date: AppDate,
    val title: String,
    val description: String?,
) : CommandUseCase.CommandModel {
    fun toEntity() = AssistanceInteraction.parse(
        id = id,
        date = date,
        title = title,
        description = description,
    )
}
