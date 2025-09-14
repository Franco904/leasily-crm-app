package com.fstengineering.leasilycrm.propertyDetails.composables.items

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.House
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.constants.APARTMENT_PROPERTY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.PropertyType

@Composable
fun PropertyTypeDetailsItem(
    propertyType: PropertyType?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = when {
                propertyType?.id == APARTMENT_PROPERTY_TYPE_ID -> Icons.Outlined.Apartment
                else -> Icons.Outlined.House
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = propertyType?.name ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
