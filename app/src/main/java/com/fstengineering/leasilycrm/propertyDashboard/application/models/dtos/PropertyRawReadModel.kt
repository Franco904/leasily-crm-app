package com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class PropertyRawReadModel(
    val id: Long,
    val ownerId: Long,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
)
