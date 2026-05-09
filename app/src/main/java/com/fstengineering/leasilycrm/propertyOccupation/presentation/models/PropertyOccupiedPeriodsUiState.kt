package com.fstengineering.leasilycrm.propertyOccupation.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

sealed interface PropertyOccupiedPeriodsUiState {
    data object Loading : PropertyOccupiedPeriodsUiState

    data class Loaded(
        val all: ImmutableMap<AppYearMonth, ImmutableList<PropertyOccupiedDateUiState>>,
        val selected: ImmutableMap<AppYearMonth, ImmutableList<PropertyOccupiedDateUiState>>,
    ) : PropertyOccupiedPeriodsUiState {
        fun setPeriodsSelection(selectedYear: Int): Loaded {
            val selected = all
                .mapValues { (_, dates) ->
                    dates.filter { it.year == selectedYear }.toPersistentList()
                }
                .toPersistentMap()

            return copy(selected = selected)
        }
    }
}
