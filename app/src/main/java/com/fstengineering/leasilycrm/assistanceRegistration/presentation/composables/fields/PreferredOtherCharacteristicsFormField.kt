package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.fields

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicSelectableChip
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PreferredOtherCharacteristicsFormField(
    form: AssistanceDraftFormUiState.Loaded,
    onFormChange: (AssistanceDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    val otherCharacteristics = remember { PropertyCharacteristic.entries.toImmutableList() }

    FlowRow(
        modifier = modifier
    ) {
        otherCharacteristics.forEach { characteristic ->
            val onSelect = remember(characteristic, form, onFormChange) {
                {
                    onAssistancePropertyCharacteristicSelect(
                        characteristic = characteristic,
                        form = form,
                        onFormChange = onFormChange,
                    )
                }
            }

            CharacteristicSelectableChip(
                label = characteristic.name,
                isSelected = characteristic in form.preferredPropertyCharacteristics,
                onSelect = onSelect,
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

private fun onAssistancePropertyCharacteristicSelect(
    characteristic: PropertyCharacteristic,
    form: AssistanceDraftFormUiState.Loaded,
    onFormChange: (AssistanceDraftFormUiState.Loaded) -> Unit,
) {
    val selectedCharacteristics = form.preferredPropertyCharacteristics
        .toPersistentList()

    val newForm = form.copy(
        preferredPropertyCharacteristics = selectedCharacteristics.mutate { selectedOnes ->
            if (characteristic in selectedOnes) {
                selectedOnes.remove(characteristic)
            } else {
                selectedOnes.add(characteristic)
            }
        }
    )

    onFormChange(newForm)
}
