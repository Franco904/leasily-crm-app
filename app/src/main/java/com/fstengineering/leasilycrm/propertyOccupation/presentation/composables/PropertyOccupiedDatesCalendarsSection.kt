package com.fstengineering.leasilycrm.propertyOccupation.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import com.fstengineering.leasilycrm.core.presentation.composables.OccupiedPeriodsCalendar
import com.fstengineering.leasilycrm.propertyOccupation.presentation.models.PropertyOccupiedPeriodsUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

@Composable
fun OccupiedDatesCalendarsSection(
    occupiedPeriods: PropertyOccupiedPeriodsUiState.Loaded,
    stayColorToTenantsNames: ImmutableList<Triple<Long, @Composable () -> Color, String>>,
    hasUnavailablePeriod: Boolean,
    onViewPeriod: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stayColorToTenantsNamesChunked =
        when {
            stayColorToTenantsNames.isEmpty() -> {
                persistentListOf(persistentListOf(), persistentListOf())
            }

            stayColorToTenantsNames.size == 1 -> {
                val first = stayColorToTenantsNames.first()
                persistentListOf(persistentListOf(first), persistentListOf())
            }

            else -> {
                stayColorToTenantsNames
                    .chunked((stayColorToTenantsNames.size.toFloat() / 2).roundToInt())
                    .toImmutableList()
            }
        }

    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (stayColorToTenantsNamesChunked[1].isNotEmpty()) Modifier.widthIn(max = 360.dp)
                        else Modifier
                    )
            ) {
                stayColorToTenantsNamesChunked[0]
                    .forEach { (stayId, stayTenantColor, tenantName) ->
                        ColoredLabel(
                            color = stayTenantColor(),
                            label = tenantName,
                            onClick = { onViewPeriod(stayId) },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
            }
            if (stayColorToTenantsNamesChunked[1].isNotEmpty()) {
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 360.dp)
                ) {
                    stayColorToTenantsNamesChunked[1]
                        .forEach { (stayId, stayTenantColor, tenantName) ->
                            ColoredLabel(
                                color = stayTenantColor(),
                                label = tenantName,
                                onClick = { onViewPeriod(stayId) },
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                }
            }
        }
        if (hasUnavailablePeriod) {
            ColoredLabel(label = stringResource(R.string.stay_type_unavailable_period_title))
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    occupiedPeriods.selected.forEach { (yearMonth, occupiedDates) ->
        val isNotFirst = yearMonth != occupiedPeriods.selected.keys.first()
        if (isNotFirst) Spacer(modifier = Modifier.height(8.dp))

        val occupiedDatesStates = occupiedDates
            .map { it.toCalendarState() }
            .toImmutableList()

        OccupiedPeriodsCalendar(
            month = yearMonth.month,
            year = yearMonth.year,
            occupiedPeriods = yearMonth to occupiedDatesStates,
            onViewPeriod = onViewPeriod,
        )
    }
}
