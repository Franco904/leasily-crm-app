package com.fstengineering.leasilycrm.core.presentation.composables.details

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun IconValueDetailsItem(
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    @DrawableRes iconDrawable: Int? = null,
    isEmptyText: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        when {
            icon != null -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            iconDrawable != null -> {
                Icon(
                    painter = painterResource(iconDrawable),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = when {
                isEmptyText -> MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
                else -> MaterialTheme.typography.bodyLarge
            },
            color = when {
                isEmptyText -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.onSurface
            },
        )
    }
}
