package com.fstengineering.leasilycrm.stayRegistration.composables.fields

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun StaysObservationsFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    isStayFromOtherRealtor: Boolean = false,
) {
    FormTextField(
        label = stringResource(
            if (isStayFromOtherRealtor) R.string.stay_registration_unavailable_period_observations_label
            else R.string.stay_registration_observations_label
        ),
        value = value ?: "",
        onValueChange = onValueChange,
        isSingleLine = false,
        maxLength = 2000,
        onDone = { onDone() },
        modifier = modifier
            .height(180.dp)
    )
}
