package com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase

data class DeleteAssistanceCommand(val id: Long) : CommandUseCase.CommandModel
