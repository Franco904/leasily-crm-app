package com.fstengineering.leasilycrm.atendimentoRegistration.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

data class InteracaoReadModel(
    val id: Long,
    val date: AppDate,
    val title: String,
    val description: String?,
)
