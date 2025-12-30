package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface StayValidator {
    fun validate(
        stay: Stay,
        stayBeforeEdit: Stay?,
    ): AppResultCompound<Unit, DomainError>
}
