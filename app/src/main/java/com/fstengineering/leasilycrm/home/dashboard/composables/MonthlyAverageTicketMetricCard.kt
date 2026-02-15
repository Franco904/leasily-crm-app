package com.fstengineering.leasilycrm.home.dashboard.composables

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
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import com.fstengineering.leasilycrm.core.presentation.theme.deepPurple
import com.fstengineering.leasilycrm.core.domain.models.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import com.fstengineering.leasilycrm.core.presentation.composables.charts.AppRowChart
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelAbbreviationByNumber
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun MonthlyAverageTicketMetricCard(
    averageTicketPerMonth: ImmutableMap<CalendarMonthYear, AppDecimal>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val deepPurple = deepPurple

    val rows = remember(averageTicketPerMonth) {
        val rows = averageTicketPerMonth.entries
            .map { (monthYear, averageTicket) ->
                Bars(
                    label = context
                        .getMonthLabelAbbreviationByNumber(monthNumber = monthYear.month)
                        .lowercase()
                        .capitalizeFirst(),
                    values = persistentListOf(
                        Bars.Data(
                            value = averageTicket.toDouble(),
                            color = Brush.linearGradient(
                                0.0f to deepPurple,
                                1.0f to deepPurple,
                            ),
                        ),
                    ),
                )
            }
            .toPersistentList()

        rows
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
                text = stringResource(R.string.dashboard_average_monthly_ticket_metric_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.dashboard_average_monthly_ticket_metric_description),
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
                    height = 520.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(32.dp))
                ColoredLabel(
                    color = deepPurple,
                    label = stringResource(R.string.dashboard_average_ticket_body_text),
                    mustEllipsizeText = false,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
