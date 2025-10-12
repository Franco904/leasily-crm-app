package com.fstengineering.leasilycrm.core.presentation.composables.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppRowChart(
    rows: ImmutableList<Bars>,
    height: Dp,
    modifier: Modifier = Modifier,
    rowThickness: Dp = 28.dp,
) {
    RowChart(
        data = rows,
        barProperties = BarProperties(
            thickness = rowThickness,
            spacing = 2.dp,
            cornerRadius = Bars.Data.Radius.Circular(6.dp),
        ),
        labelProperties = LabelProperties(
            enabled = true,
            padding = 8.dp,
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ),
        labelHelperProperties = LabelHelperProperties(enabled = false),
        indicatorProperties = VerticalIndicatorProperties(
            textStyle = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            padding = 8.dp,
        ),
        popupProperties = PopupProperties(
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.surfaceContainer,
            ),
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        dividerProperties = DividerProperties(enabled = false),
        gridProperties = GridProperties(
            xAxisProperties = GridProperties.AxisProperties(
                color = Brush.linearGradient(
                    0.0f to MaterialTheme.colorScheme.outlineVariant,
                    1.0f to MaterialTheme.colorScheme.outlineVariant,
                ),
            ),
            yAxisProperties = GridProperties.AxisProperties(
                color = Brush.linearGradient(
                    0.0f to MaterialTheme.colorScheme.outlineVariant,
                    1.0f to MaterialTheme.colorScheme.outlineVariant,
                ),
            ),
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        modifier = modifier
            .size(width = 360.dp, height = height)
    )
}
