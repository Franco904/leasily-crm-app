package com.fstengineering.leasilycrm.leadTenantRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst

@Composable
fun LeadTenantNameFormField(
    tenantName: String?,
    onTenantNameChange: (String?) -> Unit,
    error: Int?,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(R.string.lead_tenant_registration_name_label),
        value = tenantName ?: "",
        onValueChange = { onTenantNameChange(it.capitalizeFirst()) },
        isInvalid = error != null,
        supportingText = stringResource(error ?: R.string.inline_required),
        maxLength = 100,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
