package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface PropertyOwnerValidator {
    fun validate(
        propertyOwner: PropertyOwner,
    ): AppResultCompound<Unit, ValidationError>
}
