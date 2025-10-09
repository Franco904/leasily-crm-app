package com.fstengineering.leasilycrm.propertyStays.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun YearFilterField(
    selectedYear: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.property_stays_year_filter_field_placeholder),
        value = selectedYear.toString(),
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
