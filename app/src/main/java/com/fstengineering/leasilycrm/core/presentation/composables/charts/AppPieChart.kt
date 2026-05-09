package com.fstengineering.leasilycrm.core.presentation.composables.charts

import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
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
    var showAnimation by rememberSaveable { mutableStateOf(true) }

    PieChart(
        data = pies,
        onPieClick = onPieClick,
        selectedScale = 1.02f,
        scaleAnimEnterSpec = if (showAnimation) tween(500) else snap(),
        colorAnimEnterSpec = if (showAnimation) tween(300) else snap(),
        colorAnimExitSpec = if (showAnimation) tween(300) else snap(),
        scaleAnimExitSpec = if (showAnimation) tween(300) else snap(),
        spaceDegreeAnimExitSpec = if (showAnimation) tween(300) else snap(),
        style = Pie.Style.Stroke(width = 48.dp),
        modifier = modifier
            .size(240.dp)
            .onGloballyPositioned { showAnimation = false }
    )
}
