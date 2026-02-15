package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class PropertyOwnerFile(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val uriString: String,
    val name: String,
    val propertyOwnerId: Long,
    val isDeleted: Boolean,
)
