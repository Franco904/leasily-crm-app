package com.fstengineering.leasilycrm.dashboard.presentation.composables

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.charts.AppRowChart
import com.fstengineering.leasilycrm.core.presentation.composables.charts.labels.AppRowChartChunkedColoredLabels
import com.fstengineering.leasilycrm.core.presentation.theme.customColorsV4
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelAbbreviationByNumber
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.roundToInt

@Composable
fun MonthlyCommissionInvoicePerTop3NeighborhoodsMetricCard(
    commissionInvoicePerTop3NeighborhoodsPerMonth: ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val rowColors = customColorsV4

    val neighborhoods = remember(commissionInvoicePerTop3NeighborhoodsPerMonth) {
        commissionInvoicePerTop3NeighborhoodsPerMonth.entries
            .flatMap { it.value.entries.map { neighborhoodToCount -> neighborhoodToCount.key } }
            .toPersistentSet()
    }

    val neighborhoodsToColors = neighborhoods
        .associateWith { neighborhood ->
            val i = neighborhoods.indexOf(neighborhood)
            rowColors[i % rowColors.size]
        }
        .toPersistentMap()

    val rows = remember(commissionInvoicePerTop3NeighborhoodsPerMonth) {
        val rows = commissionInvoicePerTop3NeighborhoodsPerMonth.entries
            .map { (monthYear, neighborhoodToCommissionInvoice) ->
                Bars(
                    label = context
                        .getMonthLabelAbbreviationByNumber(monthNumber = monthYear.month)
                        .lowercase()
                        .capitalizeFirst(),
                    values = neighborhoodToCommissionInvoice.entries
                        .mapIndexed { i, (neighborhoodName, commissionInvoice) ->
                            val rowColor = neighborhoodsToColors.entries
                                .find { it.key == neighborhoodName }
                                ?.value
                                ?: rowColors[i % rowColors.size]

                            Bars.Data(
                                label = neighborhoodName,
                                value = commissionInvoice.doubleValue,
                                color = Brush.linearGradient(
                                    0.0f to rowColor,
                                    1.0f to rowColor,
                                ),
                            )
                        }
                        .toPersistentList()
                )
            }
            .toPersistentList()

        rows
    }

    val neighborhoodsWithColorsChunked = remember(neighborhoodsToColors) {
        when {
            neighborhoodsToColors.entries.isEmpty() -> {
                persistentListOf(persistentListOf(), persistentListOf())
            }

            neighborhoodsToColors.entries.size == 1 -> {
                val first = neighborhoodsToColors.entries.first()
                persistentListOf(persistentListOf(first), persistentListOf())
            }

            else -> {
                neighborhoodsToColors
                    .entries
                    .chunked((neighborhoodsToColors.entries.size.toDouble() / 2).roundToInt())
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
                text = stringResource(R.string.dashboard_monthly_commission_invoice_per_top3_neighborhoods_metric_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.dashboard_monthly_commission_invoice_per_top3_neighborhoods_metric_description),
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
                    rowThickness = (16.0 / (neighborhoodsToColors.size.toDouble() / 2.5))
                        .coerceIn(16.0..28.0).dp,
                    height = (520.0 + (12.0 * rows.size.toDouble() * neighborhoodsToColors.size.toDouble()))
                        .coerceAtLeast(520.0).dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
            }
            if (neighborhoodsWithColorsChunked.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                AppRowChartChunkedColoredLabels(rowsChunked = neighborhoodsWithColorsChunked)
            }
        }
    }
}
