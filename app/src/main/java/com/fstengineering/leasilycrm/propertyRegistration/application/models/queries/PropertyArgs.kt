package com.fstengineering.leasilycrm.propertyRegistration.application.models.queries

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class PropertyArgs(val id: Long) : QueryUseCase.Args
