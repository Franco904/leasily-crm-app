package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        ElevatedCard(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.elevatedCardElevation(2.dp),
            onClick = onClick,
            modifier = modifier
        ) {
            content()
        }
    } else {
        ElevatedCard(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.elevatedCardElevation(2.dp),
            modifier = modifier
        ) {
            content()
        }
    }
}
