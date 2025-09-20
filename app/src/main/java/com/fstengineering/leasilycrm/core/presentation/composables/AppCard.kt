package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.medium

    val borderStroke = when {
        isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else -> null
    }

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.38f)
        else -> Color.Unspecified
    }

    val clickableCardModifier = modifier
        .then(
            if (borderStroke != null) Modifier.border(borderStroke, shape)
            else Modifier
        )

    if (onClick != null) {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(containerColor = backgroundColor),
            elevation = CardDefaults.elevatedCardElevation(
                if (isSelected) 0.dp else 2.dp,
            ),
            shape = MaterialTheme.shapes.medium,
            onClick = onClick,
            modifier = clickableCardModifier
        ) {
            content()
        }
    } else {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(2.dp),
            shape = MaterialTheme.shapes.medium,
            modifier = boxModifier.then(modifier)
        ) {
            content()
        }
    }
}
