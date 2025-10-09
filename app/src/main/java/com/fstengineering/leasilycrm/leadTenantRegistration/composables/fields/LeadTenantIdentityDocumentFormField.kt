package com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun LeadTenantIdentityDocumentFormField(
    identityDocument: String?,
    onIdentityDocumentChange: (String?) -> Unit,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(R.string.lead_tenant_registration_identity_document_label),
        value = identityDocument ?: "",
        onValueChange = { onIdentityDocumentChange(it) },
        maxLength = 50,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
