package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface AssistanceInteractionValidator {
    fun validate(
        interaction: AssistanceInteraction,
    ): AppResultCompound<Unit, ValidationError>
}
