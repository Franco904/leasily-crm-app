package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class CustomerSegment(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val name: String? = null,
    val isDeleted: Boolean = false,
    val type: CustomerSegmentType? = null,
    val userId: Long? = null,
)
