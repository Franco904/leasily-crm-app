package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class PropertyFile(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val uriString: String,
    val isMainPicture: Boolean,
)
