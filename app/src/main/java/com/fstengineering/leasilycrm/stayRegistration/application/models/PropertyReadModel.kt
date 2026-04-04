package com.fstengineering.leasilycrm.stayRegistration.application.models

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import kotlinx.collections.immutable.ImmutableList

data class PropertyReadModel(
    val id: Long?,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
    val staysDates: ImmutableList<Pair<AppDateTime, AppDateTime>>,
)
