package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun PropertyTypeFormField(
    propertyTypes: ImmutableList<PropertyType>,
    selectedPropertyType: PropertyType?,
    onPropertyTypeChange: (PropertyType?) -> Unit,
    error: Int?,
    modifier: Modifier = Modifier,
) {
    AppRadioGroup(
        radioOptions = propertyTypes.map { it.name }.toImmutableList(),
        selectedOption = selectedPropertyType?.name,
        onOptionSelected = { typeName ->
            val type = propertyTypes.find { it.name == typeName }
            onPropertyTypeChange(type)
        },
        isInvalid = error != null,
        supportingText = stringResource(error ?: R.string.inline_required),
        modifier = modifier
    )
}
