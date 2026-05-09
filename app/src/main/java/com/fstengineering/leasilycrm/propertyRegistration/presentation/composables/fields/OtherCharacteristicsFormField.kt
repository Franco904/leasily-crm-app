package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicSelectableChip
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun OtherCharacteristicsFormField(
    form: PropertyDraftFormUiState.Loaded,
    onFormChange: (PropertyDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    val otherCharacteristics = remember { PropertyCharacteristic.entries.toImmutableList() }

    FlowRow(
        modifier = modifier
    ) {
        FlowRow(
            modifier = modifier
        ) {
            otherCharacteristics.forEach { characteristic ->
                val onSelect = remember(characteristic, form, onFormChange) {
                    {
                        onPropertyCharacteristicSelect(
                            characteristic = characteristic,
                            form = form,
                            onFormChange = onFormChange,
                        )
                    }
                }

                CharacteristicSelectableChip(
                    label = characteristic.name,
                    isSelected = characteristic in form.characteristics,
                    onSelect = onSelect,
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

private fun onPropertyCharacteristicSelect(
    characteristic: PropertyCharacteristic,
    form: PropertyDraftFormUiState.Loaded,
    onFormChange: (PropertyDraftFormUiState.Loaded) -> Unit,
) {
    val selectedCharacteristics = form.characteristics.toPersistentList()

    val newForm = form.copy(
        characteristics = selectedCharacteristics.mutate { selectedOnes ->
            if (characteristic in selectedOnes) {
                selectedOnes.remove(characteristic)
            } else {
                selectedOnes.add(characteristic)
            }
        }
    )

    onFormChange(newForm)
}
