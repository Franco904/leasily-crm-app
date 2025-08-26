package com.fstengineering.leasilycrm.core.domain.models

data class CustomerSegment(
    val id: Int?,
    val name: String?,
    val isDeleted: Boolean,
    val type: CustomerSegmentType,
    val userId: Int?,
)
