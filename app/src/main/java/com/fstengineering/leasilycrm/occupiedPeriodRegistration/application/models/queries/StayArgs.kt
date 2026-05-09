package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class StayArgs(val id: Long?) : QueryUseCase.Args
