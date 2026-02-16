package com.fstengineering.leasilycrm.assistanceRegistration.composables.fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime

@Composable
fun AssistanceInteractionDateFormField(
    interactionDate: AppDate?,
    error: Int?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.assistance_registration_interaction_registration_interaction_date_label),
        value = when {
            interactionDate != null -> {
                stringResource(
                    R.string.assistance_registration_interaction_registration_interaction_date_value,
                    interactionDate.dayOfMonth.formatToTwoDigitsDateTime(),
                    interactionDate.month.formatToTwoDigitsDateTime(),
                    interactionDate.year,
                )
            }

            else -> ""
        },
        suffix = {
            Icon(
                imageVector = Icons.Outlined.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        onTap = onTap,
        isReadOnly = true,
        isInvalid = error != null,
        supportingText = stringResource(error ?: R.string.inline_required),
        modifier = modifier
    )
}
