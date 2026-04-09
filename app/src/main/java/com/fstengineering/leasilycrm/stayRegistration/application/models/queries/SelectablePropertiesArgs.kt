package com.fstengineering.leasilycrm.stayRegistration.application.models.queries

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class SelectablePropertiesArgs(
    val matching: String?,
    val stayId: Long?,
) : QueryUseCase.Args
