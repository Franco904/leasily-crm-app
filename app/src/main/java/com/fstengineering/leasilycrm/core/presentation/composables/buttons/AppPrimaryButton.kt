package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AppPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    prefixIcon: ImageVector? = null,
    prefixIconDrawable: Int? = null,
    suffixIcon: ImageVector? = null,
) {
    ElevatedButton(
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        onClick = onClick,
        modifier = modifier
    ) {
        when {
            prefixIcon != null -> {
                Icon(
                    imageVector = prefixIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            prefixIconDrawable != null -> {
                Icon(
                    painter = painterResource(prefixIconDrawable),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        if (suffixIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = suffixIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}
