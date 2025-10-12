package com.fstengineering.leasilycrm.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.customColorsV3
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.charts.AppRowChart
import com.fstengineering.leasilycrm.core.presentation.composables.charts.labels.AppRowChartChunkedColoredLabels
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelAbbreviationByNumber
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.roundToInt

@Composable
fun MonthlyNewStayBookingsPerPropertyOwnerSegmentMetricCard(
    newStayBookingsPerPropertyOwnerSegmentPerMonth: ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val rowColors = customColorsV3

    var rowsWithColors by remember { mutableStateOf(persistentMapOf<String, Color>()) }

    val rows = remember(newStayBookingsPerPropertyOwnerSegmentPerMonth) {
        val rows = newStayBookingsPerPropertyOwnerSegmentPerMonth.entries
            .mapIndexed { outerIndex, (monthYear, propertyOwnerSegmentToBookingsCount) ->
                Bars(
                    label = context
                        .getMonthLabelAbbreviationByNumber(monthNumber = monthYear.month)
                        .lowercase()
                        .capitalizeFirst(),
                    values = propertyOwnerSegmentToBookingsCount.entries
                        .mapIndexed { i, (propertyOwnerSegmentName, bookingsCount) ->
                            val rowColor = rowsWithColors.entries
                                .find { it.key == propertyOwnerSegmentName }
                                ?.value
                                ?: rowColors[i % rowColors.size]

                            val row = Bars.Data(
                                label = propertyOwnerSegmentName,
                                value = bookingsCount.toDouble(),
                                color = Brush.linearGradient(
                                    0.0f to rowColor,
                                    1.0f to rowColor,
                                ),
                            )

                            if (outerIndex == 0) {
                                rowsWithColors = rowsWithColors.put(row.label!!, rowColor)
                            }
                            row
                        }
                        .toPersistentList()
                )
            }
            .toPersistentList()

        rows
    }

    val rowsWithColorsChunked = remember(rowsWithColors) {
        when {
            rowsWithColors.entries.isEmpty() -> {
                persistentListOf(persistentListOf(), persistentListOf())
            }

            rowsWithColors.entries.size == 1 -> {
                val first = rowsWithColors.entries.first()
                persistentListOf(persistentListOf(first), persistentListOf())
            }

            else -> {
                rowsWithColors
                    .entries
                    .chunked((rowsWithColors.entries.size.toDouble() / 2).roundToInt())
                    .map { it.toImmutableList() }
                    .toImmutableList()
            }
        }
    }

    AppCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_monthly_new_bookings_per_property_owner_segment_metric_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.dashboard_monthly_new_bookings_per_property_owner_segment_metric_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (rows.isEmpty()) {
                CrmMetricChartEmptySection(
                    icon = Icons.Outlined.BarChart,
                    labelId = R.string.dashboard_crm_metric_chart_last_12_months_empty_text,
                    modifier = Modifier
                        .padding(vertical = 64.dp)
                )
            } else {
                AppRowChart(
                    rows = rows,
                    rowThickness = (16.0 / (rowsWithColors.size.toDouble() / 2.5))
                        .coerceIn(16.0..28.0).dp,
                    height = (520.0 + (12.0 * rows.size.toDouble() * rowsWithColors.size.toDouble()))
                        .coerceAtLeast(520.0).dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
            }
            if (rowsWithColorsChunked.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                AppRowChartChunkedColoredLabels(rowsChunked = rowsWithColorsChunked)
            }
        }
    }
}
