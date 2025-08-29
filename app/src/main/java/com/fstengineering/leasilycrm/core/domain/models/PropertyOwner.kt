package com.fstengineering.leasilycrm.core.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PropertyOwner(
    val id: Int? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val paymentKey: String? = null,
    val isDeleted: Boolean = false,
    val customerSegment: CustomerSegment? = null,
    val files: ImmutableList<PropertyOwnerFile> = persistentListOf(),
    val userId: Int? = null,
)
