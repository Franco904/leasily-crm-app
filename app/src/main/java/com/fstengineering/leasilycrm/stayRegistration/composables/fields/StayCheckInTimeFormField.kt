package com.fstengineering.leasilycrm.stayRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime

@Composable
fun StayCheckInTimeFormField(
    checkInHour: Int?,
    checkInMinute: Int?,
    error: Int?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    hasValidation: Boolean = true,
    isStayFromOtherRealtor: Boolean = false,
) {
    FormTextField(
        label = stringResource(
            if (isStayFromOtherRealtor) R.string.stay_registration_unavailable_period_start_time_label
            else R.string.stay_registration_check_in_time_label
        ),
        value = when {
            checkInHour != null && checkInMinute != null -> {
                stringResource(
                    R.string.stay_registration_time_value,
                    checkInHour.formatToTwoDigitsDateTime(),
                    checkInMinute.formatToTwoDigitsDateTime(),
                )
            }

            else -> ""
        },
        isInvalid = !hasValidation && error != null,
        supportingText = if (hasValidation) {
            stringResource(error ?: R.string.inline_required)
        } else null,
        onTap = onTap,
        isReadOnly = true,
        modifier = modifier
    )
}
