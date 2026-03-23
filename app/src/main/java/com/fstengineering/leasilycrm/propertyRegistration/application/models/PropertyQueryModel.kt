package com.fstengineering.leasilycrm.propertyRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class PropertyQueryModel(val id: Long) : QueryUseCase.QueryModel
