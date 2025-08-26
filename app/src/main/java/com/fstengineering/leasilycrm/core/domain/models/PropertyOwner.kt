package com.fstengineering.leasilycrm.core.domain.models

data class PropertyOwner(
    val id: Int?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val isDeleted: Boolean = false,
    val customerSegment: CustomerSegment?,
    val files: List<PropertyOwnerFile>,
    val userId: Int,
)
