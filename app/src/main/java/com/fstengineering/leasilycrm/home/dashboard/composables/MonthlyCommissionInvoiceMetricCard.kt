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
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import com.fstengineering.leasilycrm.core.presentation.composables.charts.AppRowChart
import com.fstengineering.leasilycrm.core.presentation.theme.teal
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelAbbreviationByNumber
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun MonthlyCommissionInvoiceMetricCard(
    commissionInvoicePerMonth: ImmutableMap<CalendarMonthYear, AppDecimal>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val teal = teal

    val totalLast12Months = commissionInvoicePerMonth.entries.sumOf { it.value.decimal }

    val rows = remember(commissionInvoicePerMonth) {
        val rows = commissionInvoicePerMonth.entries
            .map { (monthYear, commissionInvoice) ->
                Bars(
                    label = context
                        .getMonthLabelAbbreviationByNumber(monthNumber = monthYear.month)
                        .lowercase()
                        .capitalizeFirst(),
                    values = persistentListOf(
                        Bars.Data(
                            value = commissionInvoice.doubleValue,
                            color = Brush.linearGradient(
                                0.0f to teal,
                                1.0f to teal,
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
                text = stringResource(R.string.dashboard_monthly_commission_invoice_metric_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.dashboard_monthly_commission_invoice_metric_description),
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
                    text = stringResource(
                        R.string.dashboard_plus_generic_body_text,
                        stringResource(
                            R.string.reais_currency_value,
                            totalLast12Months
                                .toString()
                                .replace(".", ","),
                        ),
                    ),
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
                    color = teal,
                    label = stringResource(R.string.dashboard_commission_invoice_body_text),
                    mustEllipsizeText = false,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
