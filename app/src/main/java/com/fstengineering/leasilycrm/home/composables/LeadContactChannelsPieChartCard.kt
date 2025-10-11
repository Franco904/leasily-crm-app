package com.fstengineering.leasilycrm.home.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import com.fstengineering.leasilycrm.core.presentation.utils.formatPercent
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.roundToInt

@Composable
fun LeadContactChannelsPieChartCard(
    leadContactChannelDistribution: ImmutableList<Pair<String, Double>>,
    modifier: Modifier = Modifier,
) {
    val pieColors = customColors

    var pies by remember(leadContactChannelDistribution) {
        val pies = leadContactChannelDistribution
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
                    .chunked((pies.size.toFloat() / 2).roundToInt())
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
                text = stringResource(R.string.dashboard_lead_contact_channels_metric_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (pies.isEmpty()) {
                CrmMetricChartEmptySection(
                    icon = Icons.Outlined.PieChart,
                    modifier = Modifier
                        .padding(vertical = 64.dp)
                )
            } else {
                PieChart(
                    data = pies,
                    onPieClick = { clickedPie ->
                        pies = pies
                            .mapIndexed { i, pie ->
                                pie.copy(selected = i == pies.indexOf(clickedPie) && !pie.selected)
                            }
                            .toPersistentList()
                    },
                    selectedScale = 1.02f,
                    scaleAnimEnterSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
                    colorAnimEnterSpec = tween(300),
                    colorAnimExitSpec = tween(300),
                    scaleAnimExitSpec = tween(300),
                    spaceDegreeAnimExitSpec = tween(300),
                    style = Pie.Style.Stroke(width = 48.dp),
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            if (piesChunked.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                LeadContactChannelColoredLabels(piesChunked = piesChunked)
            }
        }
    }
}

@Composable
fun LeadContactChannelColoredLabels(
    piesChunked: ImmutableList<ImmutableList<Pie>>,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 360.dp)
        ) {
            piesChunked[0].forEach { pie ->
                ColoredLabel(
                    color = pie.color,
                    label = when {
                        pie.label == null -> stringResource(R.string.unknown_text)
                        else -> "${pie.label} (${pie.data.toString().formatPercent()}%)"
                    },
                    mustEllipsizeText = false,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 360.dp)
        ) {
            piesChunked[1].forEach { pie ->
                ColoredLabel(
                    color = pie.color,
                    label = when {
                        pie.label == null -> stringResource(R.string.unknown_text)
                        else -> "${pie.label} (${pie.data.toString().formatPercent()}%)"
                    },
                    mustEllipsizeText = false,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
