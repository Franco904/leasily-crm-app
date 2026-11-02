package com.fstengineering.leasilycrm.imovelOccupation.presentation.mappers

import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelOccupiedDateReadModel
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelOccupiedPeriodsReadModel
import com.fstengineering.leasilycrm.imovelOccupation.presentation.models.ImovelOccupiedDateUiState
import com.fstengineering.leasilycrm.imovelOccupation.presentation.models.ImovelOccupiedPeriodsUiState
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun ImovelOccupiedPeriodsReadModel.toUiState(): ImovelOccupiedPeriodsUiState.Loaded {
    val allOccupiedPeriods = occupiedPeriods
        .mapValues { (_, dates) -> dates.map { it.toUiState() }.toPersistentList() }
        .toPersistentMap()

    return ImovelOccupiedPeriodsUiState.Loaded(
        all = allOccupiedPeriods,
        selected = persistentMapOf(),
    )
}

fun ImovelOccupiedDateReadModel.toUiState() = ImovelOccupiedDateUiState(
    dayOfMonth = date.dayOfMonth.toString(),
    year = date.year,
    firstHalfPeriodIndex = firstHalfPeriodIndex,
    secondHalfPeriodIndex = secondHalfPeriodIndex,
    periodId = periodId,
    tenantName = tenantName,
)
