package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface PropertyOwnerValidator {
    fun validate(
        propertyOwner: PropertyOwner,
        isThereStoredPropertyOwnerWithSameName: Boolean,
    ): AppResultCompound<Unit, DomainError>
}
