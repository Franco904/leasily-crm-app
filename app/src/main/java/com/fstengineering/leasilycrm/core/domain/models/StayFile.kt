package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class StayFile(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val uriString: String? = null,
    val name: String? = null,
    val stayId: Long? = null,
    val isDeleted: Boolean = false,
)
