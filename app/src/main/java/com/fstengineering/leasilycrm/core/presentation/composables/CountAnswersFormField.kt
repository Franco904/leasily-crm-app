package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CountAnswersFormField(
    countAnswers: ImmutableList<CountAnswer>,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .padding(start = 16.dp, end = 24.dp)
        ) {
            countAnswers.forEachIndexed { i, countAnswer ->
                if (i > 0) Spacer(modifier = Modifier.height(16.dp))
                CountAnswerCounter(
                    label = countAnswer.label,
                    count = countAnswer.count,
                    onCountChange = countAnswer.onCountChange,
                )
            }
        }
    }
}

@Composable
fun CountAnswerCounter(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier
                .weight(1f)
        )
        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = when {
                    count == 0 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.primary
                },
            ),
            onClick = { onCountChange((count - 1).coerceAtLeast(0)) },
            modifier = Modifier
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                tint = when {
                    count == 0 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else -> MaterialTheme.colorScheme.onPrimary
                },
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            onClick = { onCountChange(count + 1) },
            modifier = Modifier
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

data class CountAnswer(
    val label: String,
    val count: Int,
    val onCountChange: (Int) -> Unit,
)
