package com.fstengineering.leasilycrm.propertyDetails.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.House
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fstengineering.leasilycrm.core.domain.constants.APARTMENT_PROPERTY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconValueDetailsItem

@Composable
fun PropertyTypeDetailsItem(
    propertyType: PropertyType?,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        icon = when {
            propertyType?.id == APARTMENT_PROPERTY_TYPE_ID -> Icons.Outlined.Apartment
            else -> Icons.Outlined.House
        },
        value = propertyType?.name ?: "",
        modifier = modifier
    )
}
