package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.queries

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class OccupiedPeriodArgs(val periodId: Long) : QueryUseCase.Args
