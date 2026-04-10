package com.fstengineering.leasilycrm.propertyStays.composables

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
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import com.fstengineering.leasilycrm.core.presentation.composables.OccupiedPeriodsCalendar
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

@Composable
fun StaysCalendarList(
    stayColorToTenantsNames: ImmutableList<Triple<Long, @Composable () -> Color, String>>,
    stays: ImmutableList<Stay>,
    selectedYearStaysDates: StaysStructuredDatesMap,
    onViewStay: (Long) -> Unit,
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
                            onClick = { onViewStay(stayId) },
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
                                onClick = { onViewStay(stayId) },
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                }
            }
        }
        if (stays.any { it.isUnavailablePeriod }) {
            ColoredLabel(
                color = null,
                label = stringResource(R.string.stay_type_unavailable_period_title),
            )
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    selectedYearStaysDates.forEach { (monthYear, staysDates) ->
        val isNotFirst = monthYear != selectedYearStaysDates.keys.first()
        if (isNotFirst) Spacer(modifier = Modifier.height(8.dp))

        OccupiedPeriodsCalendar(
            month = monthYear.month,
            year = monthYear.year,
            occupiedPeriods = monthYear to staysDates,
            onViewPeriod = onViewStay,
        )
    }
}
