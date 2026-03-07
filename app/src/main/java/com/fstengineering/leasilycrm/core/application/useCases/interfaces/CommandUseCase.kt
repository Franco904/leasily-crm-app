package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError

interface CommandUseCase<W : CommandUseCase.CommandModel> {
    suspend operator fun invoke(command: W): AppResultCompound<Unit, BusinessRuleError>

    interface CommandModel
}
