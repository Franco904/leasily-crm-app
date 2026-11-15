package com.fstengineering.leasilycrm.imovelOcupacao.presentation.mappers

import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelOccupiedDateReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelPeriodosOcupadosReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.models.ImovelOccupiedDateUiState
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.models.ImovelPeriodosOcupadosUiState
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun ImovelPeriodosOcupadosReadModel.toUiState(): ImovelPeriodosOcupadosUiState.Loaded {
    val allOccupiedPeriods = occupiedPeriods
        .mapValues { (_, dates) -> dates.map { it.toUiState() }.toPersistentList() }
        .toPersistentMap()

    return ImovelPeriodosOcupadosUiState.Loaded(
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
