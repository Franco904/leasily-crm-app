package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import java.math.BigDecimal

@Composable
fun PropertyPriceAmountFormField(
    priceAmount: BigDecimal?,
    onPriceAmountChange: (BigDecimal?) -> Unit,
    error: Int?,
    priceAmountFieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    AppTextField(
        label = stringResource(R.string.property_registration_price_amount_label),
        value = priceAmount?.toString() ?: "",
        onValueChange = { priceAmountInput ->
            if (!priceAmountInput.isDigitsOnly()) return@AppTextField
            onPriceAmountChange(priceAmountInput.toBigDecimalOrNull())
        },
        isInvalid = error != null,
        prefix = {
            Text(
                text = "R$ ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        supportingText = stringResource(error ?: R.string.inline_required),
        keyboardType = KeyboardType.Number,
        fieldFocusRequester = priceAmountFieldFocusRequester,
        modifier = modifier
    )
}
