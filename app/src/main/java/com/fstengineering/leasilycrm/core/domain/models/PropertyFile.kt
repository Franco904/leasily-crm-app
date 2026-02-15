package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class PropertyFile(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val uriString: String,
    val propertyId: Long,
    val isMainPicture: Boolean,
    val isDeleted: Boolean,
)
