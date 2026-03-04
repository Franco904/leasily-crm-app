package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst

@Composable
fun AssistanceInteractionTitleFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    error: Int?,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.assistance_registration_interaction_registration_title_label),
        value = value ?: "",
        onValueChange = { onValueChange(it.capitalizeFirst()) },
        isInvalid = error != null,
        supportingText = stringResource(error ?: R.string.inline_required),
        maxLength = 150,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
