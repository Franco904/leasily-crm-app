package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic

data class AssistancePropertyCharacteristic(
    val propertyCharacteristic: PropertyCharacteristic,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
)
