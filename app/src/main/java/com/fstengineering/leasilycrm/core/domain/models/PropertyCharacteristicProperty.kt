package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class PropertyCharacteristicProperty(
    val propertyId: Long,
    val propertyCharacteristic: PropertyCharacteristic,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val isDeleted: Boolean,
)
