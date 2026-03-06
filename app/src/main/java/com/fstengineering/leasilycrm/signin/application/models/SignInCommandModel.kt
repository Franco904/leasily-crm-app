package com.fstengineering.leasilycrm.signin.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase

data class SignInCommandModel(val idToken: String) : CommandUseCase.CommandModel
