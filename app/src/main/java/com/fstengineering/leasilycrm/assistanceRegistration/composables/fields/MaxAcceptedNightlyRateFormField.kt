package com.fstengineering.leasilycrm.assistanceRegistration.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import java.math.BigDecimal

@Composable
fun MaxAcceptedNightlyRateFormField(
    maxAcceptedNightlyRate: BigDecimal?,
    onMaxAcceptedNightlyRateChange: (BigDecimal?) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.assistance_registration_max_accepted_nightly_rate_label),
        value = maxAcceptedNightlyRate?.toString() ?: "",
        onValueChange = { maxRateInput ->
            if (!maxRateInput.isDigitsOnly()) return@FormTextField
            onMaxAcceptedNightlyRateChange(maxRateInput.toBigDecimalOrNull())
        },
        prefix = {
            Text(
                text = "R$ ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        keyboardType = KeyboardType.Number,
        modifier = modifier
    )
}
