package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.entities.Assistance

data class AssistanceQueryModel(
    val leadTenantId: Long,
    val assistanceId: Long?,
) : QueryUseCase.QueryModel
