package com.fstengineering.leasilycrm.stayRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class StayQueryModel(val id: Long?) : QueryUseCase.QueryModel
