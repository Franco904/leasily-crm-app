package com.fstengineering.leasilycrm.core.domain.models

data class PropertyFile(
    val id: Int? = null,
    val uriString: String? = null,
    val propertyId: Int? = null,
    val isMainPicture: Boolean = false,
    val isDeleted: Boolean = false,
)
