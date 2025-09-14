package com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst

@Composable
fun LeadTenantCityFormField(
    city: String?,
    onCityChange: (String?) -> Unit,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(R.string.lead_tenant_registration_city_label),
        value = city ?: "",
        onValueChange = { onCityChange(it.capitalizeFirst()) },
        maxLength = 100,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
