package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime

@Composable
fun PreferredStayPeriodFormField(
    preferredStartDate: AppDate?,
    preferredEndDate: AppDate?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.assistance_registration_date_range_label),
        value = when {
            preferredStartDate != null && preferredEndDate != null -> {
                stringResource(
                    R.string.property_registration_date_range_value,
                    preferredStartDate.dayOfMonth.formatToTwoDigitsDateTime(),
                    preferredStartDate.month.formatToTwoDigitsDateTime(),
                    preferredEndDate.dayOfMonth.formatToTwoDigitsDateTime(),
                    preferredEndDate.month.formatToTwoDigitsDateTime(),
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
