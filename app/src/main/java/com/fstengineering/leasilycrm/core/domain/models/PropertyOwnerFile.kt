package com.fstengineering.leasilycrm.core.domain.models

data class PropertyOwnerFile(
    val id: Int?,
    val propertyOwnerFileUri: String?,
    val propertyOwnerId: Int?,
    val isDeleted: Boolean = false,
)
