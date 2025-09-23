package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class PropertyFile(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val uriString: String? = null,
    val propertyId: Long? = null,
    val isMainPicture: Boolean = false,
    val isDeleted: Boolean = false,
)
