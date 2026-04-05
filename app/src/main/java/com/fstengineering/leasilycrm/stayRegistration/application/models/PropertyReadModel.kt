package com.fstengineering.leasilycrm.stayRegistration.application.models

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import kotlinx.collections.immutable.ImmutableSet

data class PropertyReadModel(
    val id: Long?,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
    val occupiedDates: ImmutableSet<AppDate>,
)
