package com.fstengineering.leasilycrm.propertyStays.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class PropertyOccupiedPeriodsReadModel(
    val occupiedPeriods: ImmutableMap<AppYearMonth, ImmutableList<PropertyOccupiedDateReadModel>>,
)
