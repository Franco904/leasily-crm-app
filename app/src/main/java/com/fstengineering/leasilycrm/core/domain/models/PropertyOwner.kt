package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PropertyOwner(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val paymentKey: String? = null,
    val isDeleted: Boolean = false,
    val files: ImmutableList<PropertyOwnerFile> = persistentListOf(),
    val userId: Long? = null,
) {
    val isAnyFormFieldInitialized = name != null
            || phone != null
            || email != null
            || paymentKey != null
            || files.isNotEmpty()
}
