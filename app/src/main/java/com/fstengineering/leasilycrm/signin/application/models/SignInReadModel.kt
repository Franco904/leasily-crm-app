package com.fstengineering.leasilycrm.signin.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class SignInReadModel(val idToken: String) : QueryUseCase.ReadModel
