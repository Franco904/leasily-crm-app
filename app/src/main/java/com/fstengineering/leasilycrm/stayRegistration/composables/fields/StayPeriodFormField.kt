package com.fstengineering.leasilycrm.stayRegistration.composables.fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime

@Composable
fun StayPeriodFormField(
    checkInDateTime: AppDateTime?,
    checkOutDateTime: AppDateTime?,
    error: Int?,
    isEnabled: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.stay_registration_period_label),
        value = when {
            checkInDateTime != null && checkOutDateTime != null -> {
                stringResource(
                    R.string.stay_registration_date_range_value,
                    checkInDateTime.dayOfMonth.formatToTwoDigitsDateTime(),
                    checkInDateTime.month.formatToTwoDigitsDateTime(),
                    checkInDateTime.year.toString(),
                    checkOutDateTime.dayOfMonth.formatToTwoDigitsDateTime(),
                    checkOutDateTime.month.formatToTwoDigitsDateTime(),
                    checkOutDateTime.year.toString(),
                )
            }

            else -> ""
        },
        isInvalid = error != null,
        isEnabled = isEnabled,
        suffix = {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        supportingText = stringResource(error ?: R.string.inline_required),
        onTap = onTap,
        isReadOnly = true,
        modifier = modifier
    )
}
