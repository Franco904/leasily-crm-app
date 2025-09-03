package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface PropertyPriceValidator {
    fun validate(
        propertyPrice: PropertyPrice,
    ): AppResultCompound<Unit, ValidationError>
}
