package com.fstengineering.leasilycrm.propertyOwnerDetails.application.models

import kotlinx.collections.immutable.ImmutableList

data class PropertyOwnerReadModel(
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<PropertyOwnerFileReadModel>,
)
