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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppRowChartChunkedColoredLabels(
    rowsChunked: ImmutableList<ImmutableList<Map.Entry<String, Color>>>,
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
                    if (rowsChunked[1].isNotEmpty()) Modifier.widthIn(max = 360.dp)
                    else Modifier
                )
        ) {
            rowsChunked[0].forEach { (label, color) ->
                ColoredLabel(
                    color = color,
                    label = label,
                    mustEllipsizeText = false,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if (rowsChunked[1].isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 360.dp)
            ) {
                rowsChunked[1].forEach { (label, color) ->
                    ColoredLabel(
                        color = color,
                        label = label,
                        mustEllipsizeText = false,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
