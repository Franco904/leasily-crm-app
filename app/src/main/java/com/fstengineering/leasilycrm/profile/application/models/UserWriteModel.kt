package com.fstengineering.leasilycrm.profile.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UseCase

data class UserWriteModel(val creciNumber: String?) : UseCase.UseCaseParam
