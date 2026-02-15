package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import kotlinx.collections.immutable.ImmutableList

data class PropertyOwner(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val name: String,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val isDeleted: Boolean,
    val files: ImmutableList<PropertyOwnerFile>,
    val userId: Long,
) {
    val isAnyFormFieldInitialized = name != null
            || phone != null
            || email != null
            || paymentKey != null
            || files.isNotEmpty()
}
