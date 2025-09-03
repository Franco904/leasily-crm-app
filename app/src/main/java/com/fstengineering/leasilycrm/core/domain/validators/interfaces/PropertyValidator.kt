package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface PropertyValidator {
    fun validate(
        property: Property,
    ): AppResultCompound<Unit, ValidationError>
}
