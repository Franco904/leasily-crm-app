package com.fstengineering.leasilycrm.propertyStays.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun MonthYearFilterField(
    placeholder: String,
    monthYearValue: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = placeholder,
        value = monthYearValue,
        suffix = {
            Icon(
                imageVector = Icons.Outlined.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        onTap = onTap,
        isPlaceholder = true,
        isReadOnly = true,
        modifier = modifier
    )
}
