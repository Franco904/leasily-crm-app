package com.fstengineering.leasilycrm.core.domain.models

data class PropertyFile(
    val id: Long? = null,
    val uriString: String? = null,
    val propertyId: Long? = null,
    val isMainPicture: Boolean = false,
    val isDeleted: Boolean = false,
)
