package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound

interface CommandUseCase<W : CommandUseCase.CommandModel> {
    suspend operator fun invoke(command: W): AppResultCompound<Unit, ApplicationError>

    interface CommandModel
}
