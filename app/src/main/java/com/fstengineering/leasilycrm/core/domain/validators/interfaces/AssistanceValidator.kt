package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface AssistanceValidator {
    fun validate(
        assistance: Assistance,
    ): AppResultCompound<Unit, DomainError>
}
