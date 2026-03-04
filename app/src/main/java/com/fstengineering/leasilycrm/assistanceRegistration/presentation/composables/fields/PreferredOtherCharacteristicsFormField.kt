package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.fields

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicSelectableChip
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PreferredOtherCharacteristicsFormField(
    assistanceForm: AssistanceDraftFormUiState.Loaded,
    onAssistanceFormChange: (AssistanceDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
    ) {
        PropertyCharacteristic.entries.forEach { characteristic ->
            CharacteristicSelectableChip(
                label = characteristic.name,
                isSelected = characteristic in assistanceForm.preferredPropertyCharacteristics,
                onSelect = {
                    onAssistancePropertyCharacteristicSelect(
                        characteristic = characteristic,
                        assistanceForm = assistanceForm,
                        onAssistanceFormChange = onAssistanceFormChange,
                    )
                },
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

private fun onAssistancePropertyCharacteristicSelect(
    characteristic: PropertyCharacteristic,
    assistanceForm: AssistanceDraftFormUiState.Loaded,
    onAssistanceFormChange: (AssistanceDraftFormUiState.Loaded) -> Unit,
) {
    val selectedCharacteristics = assistanceForm.preferredPropertyCharacteristics
        .toPersistentList()

    val newAssistanceForm = assistanceForm.copy(
        preferredPropertyCharacteristics = selectedCharacteristics.mutate { selectedOnes ->
            if (characteristic in selectedOnes) {
                selectedOnes.remove(characteristic)
            } else {
                selectedOnes.add(characteristic)
            }
        }
    )

    onAssistanceFormChange(newAssistanceForm)
}
