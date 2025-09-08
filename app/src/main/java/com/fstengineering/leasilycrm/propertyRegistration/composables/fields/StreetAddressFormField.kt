package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst

@Composable
fun StreetAddressFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    error: Int?,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    AppTextField(
        label = stringResource(R.string.property_registration_street_address_label),
        value = value ?: "",
        onValueChange = { onValueChange(it.capitalizeFirst()) },
        isInvalid = error != null,
        supportingText = stringResource(error ?: R.string.inline_required),
        maxLength = 120,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
