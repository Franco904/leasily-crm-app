package com.fstengineering.leasilycrm.propertyStays.presentation.mappers

import com.fstengineering.leasilycrm.propertyStays.application.models.dtos.PropertyOccupiedDateReadModel
import com.fstengineering.leasilycrm.propertyStays.application.models.dtos.PropertyOccupiedPeriodsReadModel
import com.fstengineering.leasilycrm.propertyStays.presentation.models.PropertyOccupiedDateUiState
import com.fstengineering.leasilycrm.propertyStays.presentation.models.PropertyOccupiedPeriodsUiState
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun PropertyOccupiedPeriodsReadModel.toUiState(): PropertyOccupiedPeriodsUiState.Loaded {
    val allOccupiedPeriods = occupiedPeriods
        .mapValues { (_, dates) -> dates.map { it.toUiState() }.toPersistentList() }
        .toPersistentMap()

    return PropertyOccupiedPeriodsUiState.Loaded(
        all = allOccupiedPeriods,
        selected = persistentMapOf(),
    )
}

fun PropertyOccupiedDateReadModel.toUiState() = PropertyOccupiedDateUiState(
    dayOfMonth = date.dayOfMonth.toString(),
    year = date.year,
    firstHalfPeriodIndex = firstHalfPeriodIndex,
    secondHalfPeriodIndex = secondHalfPeriodIndex,
    periodId = periodId,
    tenantName = tenantName,
)
