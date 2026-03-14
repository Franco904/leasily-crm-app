package com.fstengineering.leasilycrm.leadTenantRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun LeadTenantPhoneFormField(
    phone: String?,
    onPhoneChange: (String?) -> Unit,
    error: Int?,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.property_owner_registration_phone_label),
        value = phone ?: "",
        onValueChange = onPhoneChange,
        isInvalid = error != null,
        supportingText = if (error != null) stringResource(error) else null,
        maxLength = 20,
        keyboardType = KeyboardType.Phone,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}