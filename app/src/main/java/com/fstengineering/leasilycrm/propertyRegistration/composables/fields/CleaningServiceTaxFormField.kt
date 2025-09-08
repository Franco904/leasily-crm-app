package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import java.math.BigDecimal

@Composable
fun CleaningServiceTaxFormField(
    cleaningServiceTax: BigDecimal?,
    onCleaningServiceTaxChange: (BigDecimal?) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTextField(
        label = stringResource(R.string.property_registration_cleaning_service_tax_label),
        value = cleaningServiceTax?.toString() ?: "",
        onValueChange = { cleaningServiceTaxInput ->
            if (!cleaningServiceTaxInput.isDigitsOnly()) return@AppTextField
            onCleaningServiceTaxChange(cleaningServiceTaxInput.toBigDecimalOrNull())
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
