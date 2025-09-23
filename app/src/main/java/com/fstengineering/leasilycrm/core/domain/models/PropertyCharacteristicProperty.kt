package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class PropertyCharacteristicProperty(
    val propertyId: Long? = null,
    val propertyCharacteristic: PropertyCharacteristic? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val isDeleted: Boolean = false,
)
