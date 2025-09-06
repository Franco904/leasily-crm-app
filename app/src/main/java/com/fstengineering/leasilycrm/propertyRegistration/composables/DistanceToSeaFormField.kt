package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField

@Composable
fun DistanceToSeaFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    error: Int?,
    modifier: Modifier = Modifier
) {
    AppTextField(
        label = stringResource(R.string.property_registration_distance_to_sea_label),
        value = value ?: "",
        onValueChange = { distanceToSeaInput ->
            if (!distanceToSeaInput.isDigitsOnly()) return@AppTextField
            onValueChange(distanceToSeaInput)
        },
        isInvalid = error != null,
        suffix = {
            Text(
                text = "m",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        supportingText = stringResource(error ?: R.string.inline_required),
        keyboardType = KeyboardType.Number,
        modifier = modifier
    )
}
