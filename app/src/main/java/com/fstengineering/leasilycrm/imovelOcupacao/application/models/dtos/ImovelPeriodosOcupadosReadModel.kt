package com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class ImovelPeriodosOcupadosReadModel(
    val occupiedPeriods: ImmutableMap<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>,
)
