package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime

data class PropertyCharacteristicProperty(
    val propertyId: Long? = null,
    val propertyCharacteristic: PropertyCharacteristic? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val isDeleted: Boolean = false,
)
