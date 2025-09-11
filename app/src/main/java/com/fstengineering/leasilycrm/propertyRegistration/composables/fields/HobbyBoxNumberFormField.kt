package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun HobbyBoxNumberFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(R.string.property_registration_hobby_box_number_label),
        value = value ?: "",
        onValueChange = onValueChange,
        maxLength = 20,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
