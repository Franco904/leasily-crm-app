package com.fstengineering.leasilycrm.core.presentation.composables.charts.labels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import com.fstengineering.leasilycrm.core.presentation.utils.formatPercent
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppPieChartChunkedColoredLabels(
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
                .then(
                    if (piesChunked[1].isNotEmpty()) Modifier.widthIn(max = 360.dp)
                    else Modifier
                )
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
        if (piesChunked[1].isNotEmpty()) {
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
}
