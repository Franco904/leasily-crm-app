package com.fstengineering.leasilycrm.assistanceRegistration.composables.fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime

@Composable
fun SearchedStayDateRangeFormField(
    searchedStartDate: AppDate?,
    searchedEndDate: AppDate?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.assistance_registration_date_range_label),
        value = when {
            searchedStartDate != null && searchedEndDate != null -> {
                stringResource(
                    R.string.property_registration_date_range_value,
                    searchedStartDate.dayOfMonth.formatToTwoDigitsDateTime(),
                    searchedStartDate.month.formatToTwoDigitsDateTime(),
                    searchedEndDate.dayOfMonth.formatToTwoDigitsDateTime(),
                    searchedEndDate.month.formatToTwoDigitsDateTime(),
                )
            }

            else -> ""
        },
        suffix = {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        onTap = onTap,
        isReadOnly = true,
        modifier = modifier
    )
}
