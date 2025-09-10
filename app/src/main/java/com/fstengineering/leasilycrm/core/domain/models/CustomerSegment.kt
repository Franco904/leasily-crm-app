package com.fstengineering.leasilycrm.core.domain.models

data class CustomerSegment(
    val id: Long? = null,
    val name: String? = null,
    val isDeleted: Boolean = false,
    val type: CustomerSegmentType? = null,
    val userId: Long? = null,
)
