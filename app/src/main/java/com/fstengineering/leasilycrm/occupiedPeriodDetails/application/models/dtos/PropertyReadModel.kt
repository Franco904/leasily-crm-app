package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class PropertyReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val type: PropertyType,
)
