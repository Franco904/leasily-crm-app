package com.fstengineering.leasilycrm.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.customColors
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.charts.AppPieChart
import com.fstengineering.leasilycrm.core.presentation.composables.charts.labels.AppPieChartChunkedColoredLabels
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.roundToInt

@Composable
fun LeadContactChannelsDistributionPieChartCard(
    leadContactChannelDistribution: ImmutableMap<String, Double>,
    modifier: Modifier = Modifier,
) {
    val pieColors = customColors

    var pies by remember(leadContactChannelDistribution) {
        val pies = leadContactChannelDistribution.entries
            .mapIndexed { i, (label, percent) ->
                Pie(
                    label = label,
                    data = percent,
                    color = pieColors[i % pieColors.size],
                )
            }
            .toPersistentList()

        mutableStateOf(pies)
    }

    val piesChunked =
        when {
            pies.isEmpty() -> {
                persistentListOf(persistentListOf(), persistentListOf())
            }

            pies.size == 1 -> {
                val first = pies.first()
                persistentListOf(persistentListOf(first), persistentListOf())
            }

            else -> {
                pies
                    .chunked((pies.size.toDouble() / 2).roundToInt())
                    .map { it.toImmutableList() }
                    .toImmutableList()
            }
        }

    AppCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_lead_contact_channels_distribution_metric_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.dashboard_lead_contact_channels_distribution_metric_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (pies.isEmpty()) {
                CrmMetricChartEmptySection(
                    icon = Icons.Outlined.PieChart,
                    labelId = R.string.dashboard_crm_metric_chart_empty_text,
                    modifier = Modifier
                        .padding(vertical = 64.dp)
                )
            } else {
                AppPieChart(
                    pies = pies,
                    onPieClick = { clickedPie ->
                        pies = pies
                            .mapIndexed { i, pie ->
                                pie.copy(selected = i == pies.indexOf(clickedPie) && !pie.selected)
                            }
                            .toPersistentList()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
            if (piesChunked.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                AppPieChartChunkedColoredLabels(piesChunked = piesChunked)
            }
        }
    }
}
