package com.fstengineering.leasilycrm.propertyDetails.composables.items

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import java.math.BigDecimal

@Composable
fun CleaningServiceDetailsItem(
    hasCleaningServiceIncluded: Boolean,
    cleaningServiceTax: BigDecimal?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.CleaningServices,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.property_registration_cleaning_service_tax_label),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = if (hasCleaningServiceIncluded) "R$ $cleaningServiceTax" else {
            stringResource(R.string.property_registration_does_not_include_cleaning_service_body_text)
        },
        style = when {
            hasCleaningServiceIncluded -> MaterialTheme.typography.bodyLarge
            else -> {
                MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
            }
        },
        color = when {
            hasCleaningServiceIncluded -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
    )
}
