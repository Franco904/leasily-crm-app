package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models

import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwnerFile

data class PropertyOwnerFileCommandModel(
    val id: Long?,
    val uriString: String,
    val name: String,
) {
    fun toEntity() = PropertyOwnerFile(
        id = id,
        uriString = uriString,
        name = name,
    )
}
