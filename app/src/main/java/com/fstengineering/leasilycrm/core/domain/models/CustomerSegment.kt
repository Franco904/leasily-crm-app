package com.fstengineering.leasilycrm.core.domain.models

data class CustomerSegment(
    val id: Int?,
    val name: String?,
    val type: CustomerSegmentType,
    val user: User,
)
