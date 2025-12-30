package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface PropertyPriceValidator {
    fun validate(
        propertyPrice: PropertyPrice,
        propertyPriceBeforeEdit: PropertyPrice?,
        otherPropertyPrices: List<PropertyPrice>,
    ): AppResultCompound<Unit, DomainError>
}
