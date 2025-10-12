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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.deepOrange
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
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
fun MonthlyNewStayBookingsMetricCard(
    newStayBookingsPerMonth: ImmutableMap<CalendarMonthYear, Int>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val deepOrange = deepOrange

    val totalLast12Months = newStayBookingsPerMonth.entries.sumOf { it.value }

    val rows = remember(newStayBookingsPerMonth) {
        val rows = newStayBookingsPerMonth.entries
            .map { (monthYear, bookingsCount) ->
                Bars(
                    label = context
                        .getMonthLabelAbbreviationByNumber(monthNumber = monthYear.month)
                        .lowercase()
                        .capitalizeFirst(),
                    values = persistentListOf(
                        Bars.Data(
                            value = bookingsCount.toDouble(),
                            color = Brush.linearGradient(
                                0.0f to deepOrange,
                                1.0f to deepOrange,
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
                text = stringResource(R.string.dashboard_monthly_new_bookings_metric_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.dashboard_monthly_new_bookings_metric_description),
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
                Text(
                    text = stringResource(R.string.dashboard_last_12_months_body_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        val firstStyle = SpanStyle(
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        val firstText = stringResource(
                            R.string.dashboard_plus_generic_body_text,
                            totalLast12Months,
                        )

                        val secondStyle = SpanStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        val secondText = stringResource(R.string.dashboard_stay_bookings_body_text)

                        withStyle(firstStyle) { append(firstText) }
                        withStyle(secondStyle) { append("  $secondText") }
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppRowChart(
                    rows = rows,
                    height = 520.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(32.dp))
                ColoredLabel(
                    color = deepOrange,
                    label = stringResource(R.string.dashboard_stay_bookings_qtd_body_text),
                    mustEllipsizeText = false,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
