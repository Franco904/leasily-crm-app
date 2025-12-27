package com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun LeadTenantObservationsFormField(
    observations: String?,
    onObservationsChange: (String?) -> Unit,
    fieldFocusRequester: FocusRequester,
    isLead: Boolean,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(
            when {
                isLead -> R.string.lead_tenant_registration_lead_observations_label
                else -> R.string.lead_tenant_registration_tenant_observations_label
            }
        ),
        value = observations ?: "",
        onValueChange = onObservationsChange,
        isSingleLine = false,
        maxLength = 2000,
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
            .height(180.dp)
    )
}
