package com.fstengineering.leasilycrm.core.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

data class PropertyOwner(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val paymentKey: String? = null,
    val isDeleted: Boolean = false,
    val customerSegment: CustomerSegment? = null,
    val files: ImmutableList<PropertyOwnerFile> = persistentListOf(),
    val userId: Long? = null,
)
