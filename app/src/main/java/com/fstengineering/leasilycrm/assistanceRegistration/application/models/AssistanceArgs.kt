package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class AssistanceArgs(
    val leadTenantId: Long,
    val assistanceId: Long?,
) : QueryUseCase.Args
