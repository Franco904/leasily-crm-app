package com.fstengineering.leasilycrm.core.application.models.commands

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase

data class RegisterCheckOutCommand(
    val stayId: Long,
    val tenantId: Long?,
) : CommandUseCase.CommandModel
