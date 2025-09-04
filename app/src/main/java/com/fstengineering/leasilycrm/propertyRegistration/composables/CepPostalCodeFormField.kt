package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField

@Composable
fun CepPostalCodeFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    AppTextField(
        label = stringResource(R.string.property_registration_cep_postal_code_label),
        value = value ?: "",
        onValueChange = onValueChange,
        maxLength = 20,
        keyboardType = KeyboardType.Number,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
