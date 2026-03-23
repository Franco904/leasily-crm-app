package com.fstengineering.leasilycrm.profile.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase

data class UserCommandModel(val creciNumber: String?) : CommandUseCase.CommandModel
