package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.SystemError

interface CommandUseCase<W : CommandUseCase.CommandModel> {
    suspend operator fun invoke(command: W): AppResultCompound<Unit, SystemError>

    interface CommandModel
}
