package com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class ImovelOccupiedPeriodsReadModel(
    val occupiedPeriods: ImmutableMap<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>,
)
