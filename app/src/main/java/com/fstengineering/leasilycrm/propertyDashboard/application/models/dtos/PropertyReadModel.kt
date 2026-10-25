package com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import kotlinx.collections.immutable.ImmutableList

data class PropertyReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val owner: ProprietarioReadModel,
    val currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<PropertyOccupiedDateReadModel>>,
) {
    companion object {
        fun create(
            propertyRaw: PropertyRawReadModel,
            owner: ProprietarioReadModel,
            currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<PropertyOccupiedDateReadModel>>,
        ) = PropertyReadModel(
            id = propertyRaw.id,
            mainPictureUriString = propertyRaw.mainPictureUriString,
            type = propertyRaw.type,
            neighborhood = propertyRaw.neighborhood,
            streetAddress = propertyRaw.streetAddress,
            number = propertyRaw.number,
            owner = owner,
            currentMonthOccupiedPeriods = currentMonthOccupiedPeriods,
        )
    }
}
