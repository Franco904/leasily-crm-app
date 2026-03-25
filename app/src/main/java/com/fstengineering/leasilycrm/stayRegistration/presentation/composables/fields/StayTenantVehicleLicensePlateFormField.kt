package com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun StayTenantVehicleLicensePlateFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(R.string.stay_registration_tenant_vehicle_license_plate_label),
        value = value ?: "",
        onValueChange = onValueChange,
        maxLength = 25,
        onDone = { onDone() },
        fieldFocusRequester = fieldFocusRequester,
        modifier = modifier
    )
}
