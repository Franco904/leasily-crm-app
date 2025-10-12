package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun CleaningServiceTaxFormField(
    cleaningServiceTax: String,
    onCleaningServiceTaxChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.property_registration_cleaning_service_tax_label),
        value = cleaningServiceTax,
        onValueChange = { cleaningServiceTaxInput ->
            if (!cleaningServiceTaxInput.all { it.isDigit() || it == ',' || it == '.' }) return@FormTextField
            if (cleaningServiceTaxInput.count { it == ',' || it == '.' } > 1) return@FormTextField

            onCleaningServiceTaxChange(cleaningServiceTaxInput)
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
