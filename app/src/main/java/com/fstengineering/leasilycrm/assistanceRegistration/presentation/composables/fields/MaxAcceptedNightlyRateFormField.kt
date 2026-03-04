package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun MaxAcceptedNightlyRateFormField(
    maxAcceptedNightlyRate: String,
    onMaxAcceptedNightlyRateChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.assistance_registration_max_accepted_nightly_rate_label),
        value = maxAcceptedNightlyRate,
        onValueChange = { maxRateInput ->
            if (!maxRateInput.all { it.isDigit() || it == ',' || it == '.' }) return@FormTextField
            if (maxRateInput.count { it == ',' || it == '.' } > 1) return@FormTextField

            onMaxAcceptedNightlyRateChange(maxRateInput)
        },
        prefix = {
            Text(
                text = "R$ ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        keyboardType = KeyboardType.Decimal,
        modifier = modifier
    )
}
