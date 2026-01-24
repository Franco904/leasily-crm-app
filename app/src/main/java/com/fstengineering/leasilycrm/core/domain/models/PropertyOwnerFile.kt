package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime

data class PropertyOwnerFile(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val uriString: String? = null,
    val name: String? = null,
    val propertyOwnerId: Long? = null,
    val isDeleted: Boolean = false,
)
