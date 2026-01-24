package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime

data class PropertyFile(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val uriString: String? = null,
    val propertyId: Long? = null,
    val isMainPicture: Boolean = false,
    val isDeleted: Boolean = false,
)
