package com.fstengineering.leasilycrm.stayRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun StayTenantVehicleMakeFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(R.string.stay_registration_tenant_vehicle_make_label),
        value = value ?: "",
        onValueChange = onValueChange,
        maxLength = 75,
        onDone = { onDone() },
        modifier = modifier
    )
}
