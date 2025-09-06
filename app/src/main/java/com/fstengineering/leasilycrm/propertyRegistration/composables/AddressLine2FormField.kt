package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField

@Composable
fun AddressLine2FormField(
    value: String?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    AppTextField(
        label = stringResource(R.string.property_registration_address_line_2_label),
        value = value ?: "",
        onValueChange = onValueChange,
        maxLength = 75,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
