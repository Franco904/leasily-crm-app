package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioGroup
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioOption
import com.fstengineering.leasilycrm.core.presentation.utils.presentationLabel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TypeFormField(
    selectedPropertyType: PropertyType?,
    onPropertyTypeChange: (PropertyType?) -> Unit,
    error: Int?,
    modifier: Modifier = Modifier,
) {
    val types = remember { PropertyType.entries.toImmutableList() }

    AppRadioGroup(
        radioOptions = types
            .map { type ->
                AppRadioOption(
                    type = type,
                    label = stringResource(type.presentationLabel()),
                )
            }
            .toImmutableList(),
        selectedOptionLabel = selectedPropertyType,
        onOptionSelected = { typeName ->
            val type = types.find { it == typeName }
            onPropertyTypeChange(type)
        },
        isInvalid = error != null,
        supportingText = stringResource(error ?: R.string.inline_required),
        modifier = modifier
    )
}
