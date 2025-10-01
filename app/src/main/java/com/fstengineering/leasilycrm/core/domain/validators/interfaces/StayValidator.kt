package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface StayValidator {
    fun validate(
        stay: Stay,
    ): AppResultCompound<Unit, ValidationError>
}
