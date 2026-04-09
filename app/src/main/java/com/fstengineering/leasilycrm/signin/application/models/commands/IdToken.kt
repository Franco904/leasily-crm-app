package com.fstengineering.leasilycrm.signin.application.models.commands

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase

@JvmInline
value class IdToken(val idToken: String) : CommandUseCase.CommandModel