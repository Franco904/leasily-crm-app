package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError

interface CommandUseCase<C> {
    suspend operator fun invoke(command: C): AppResultCompound<Unit, BusinessRuleError>

    interface CommandModel
}
