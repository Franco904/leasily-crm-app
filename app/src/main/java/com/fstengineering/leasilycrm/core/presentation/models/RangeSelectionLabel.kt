package com.fstengineering.leasilycrm.core.presentation.models

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class RangeSelectionLabel(
    val count: Int,
    val color: @Composable () -> Color,
) {
    None(count = 0, color = { MaterialTheme.colorScheme.surface }),
    First(count = 1, color = { MaterialTheme.colorScheme.tertiaryContainer }),
    Second(count = 2, color = { MaterialTheme.colorScheme.tertiaryContainer }),
    Third(count = 3, color = { MaterialTheme.colorScheme.tertiaryContainer });

    companion object {
        fun fromCount(count: Int) = entries.find { it.count == count } ?: None
    }
}
