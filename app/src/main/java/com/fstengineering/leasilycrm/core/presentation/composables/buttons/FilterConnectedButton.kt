package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.models.FilterConnectedButtonPosition

@Composable
fun FilterConnectedButton(
    label: String,
    isSelected: Boolean,
    position: FilterConnectedButtonPosition,
    onFilterTypeSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = { onFilterTypeSelect() },
        color = when {
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        shape = if (isSelected) CircleShape else {
            when (position) {
                FilterConnectedButtonPosition.Start -> {
                    RoundedCornerShape(
                        topStart = CircleShape.topStart,
                        topEnd = MaterialTheme.shapes.small.topEnd,
                        bottomStart = CircleShape.bottomStart,
                        bottomEnd = MaterialTheme.shapes.small.bottomEnd,
                    )
                }

                FilterConnectedButtonPosition.Center -> {
                    RoundedCornerShape(
                        topStart = MaterialTheme.shapes.small.topStart,
                        topEnd = MaterialTheme.shapes.small.topEnd,
                        bottomStart = MaterialTheme.shapes.small.bottomStart,
                        bottomEnd = MaterialTheme.shapes.small.bottomEnd,
                    )
                }

                FilterConnectedButtonPosition.End -> {
                    RoundedCornerShape(
                        topStart = MaterialTheme.shapes.small.topStart,
                        topEnd = CircleShape.topEnd,
                        bottomStart = MaterialTheme.shapes.small.bottomStart,
                        bottomEnd = CircleShape.bottomEnd,
                    )
                }
            }
        },
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 24.dp)
        )
    }
}
