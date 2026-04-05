package com.fstengineering.leasilycrm.stayRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class StayArgs(val id: Long?) : QueryUseCase.Args
