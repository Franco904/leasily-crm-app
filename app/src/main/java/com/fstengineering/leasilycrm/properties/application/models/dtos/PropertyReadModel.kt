package com.fstengineering.leasilycrm.properties.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class PropertyReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
)