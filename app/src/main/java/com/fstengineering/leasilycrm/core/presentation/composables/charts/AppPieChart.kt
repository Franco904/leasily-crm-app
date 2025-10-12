package com.fstengineering.leasilycrm.core.presentation.composables.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppPieChart(
    pies: ImmutableList<Pie>,
    onPieClick: (Pie) -> Unit,
    modifier: Modifier = Modifier,
) {
    PieChart(
        data = pies,
        onPieClick = onPieClick,
        selectedScale = 1.02f,
        scaleAnimEnterSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        colorAnimEnterSpec = tween(300),
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        style = Pie.Style.Stroke(width = 48.dp),
        modifier = modifier
            .size(240.dp)
    )
}
