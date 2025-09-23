package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class PropertyOwnerFile(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val uriString: String? = null,
    val name: String? = null,
    val propertyOwnerId: Long? = null,
    val isDeleted: Boolean = false,
)
