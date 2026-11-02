package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import kotlinx.collections.immutable.ImmutableSet

data class ImovelReadModel(
    val id: Long?,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
    val occupiedDates: ImmutableSet<AppDate>,
)
