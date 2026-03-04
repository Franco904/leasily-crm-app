package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.fields

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun AssistanceObservationsFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.assistance_registration_assistance_observations_label),
        value = value ?: "",
        onValueChange = onValueChange,
        isSingleLine = false,
        maxLength = 2000,
        modifier = modifier
            .height(180.dp)
    )
}
