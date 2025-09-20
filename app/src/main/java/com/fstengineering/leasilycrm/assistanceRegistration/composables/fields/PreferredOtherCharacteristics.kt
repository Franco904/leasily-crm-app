package com.fstengineering.leasilycrm.assistanceRegistration.composables.fields

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristicAssistance
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicSelectableChip
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PreferredOtherCharacteristics(
    assistanceForm: Assistance,
    onAssistanceFormChange: (Assistance) -> Unit,
    assistancePropertyCharacteristics: ImmutableList<AssistanceCharacteristic>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
    ) {
        assistancePropertyCharacteristics.forEach { characteristic ->
            CharacteristicSelectableChip(
                label = characteristic.name,
                isSelected = characteristic in assistanceForm.characteristics
                    .filter { !it.isDeleted }
                    .map { it.assistanceCharacteristic },
                onSelect = {
                    onSelectAssistancePropertyCharacteristic(
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

private fun onSelectAssistancePropertyCharacteristic(
    characteristic: AssistanceCharacteristic,
    assistanceForm: Assistance,
    onAssistanceFormChange: (Assistance) -> Unit,
) {
    val selectedCharacteristics = assistanceForm.characteristics
        .toPersistentList()

    val newAssistanceForm = assistanceForm.copy(
        characteristics = selectedCharacteristics.mutate { list ->
            val characteristics = list.map { it.assistanceCharacteristic }

            if (characteristic in characteristics) {
                val associatedCharacteristic =
                    list.find { it.assistanceCharacteristic == characteristic } ?: return

                if (associatedCharacteristic.assistanceId != null) {
                    val i = list.indexOf(associatedCharacteristic)
                    list[i] = associatedCharacteristic.copy(isDeleted = true)
                } else {
                    list.remove(associatedCharacteristic)
                }
            } else {
                val newCharacteristic =
                    AssistanceCharacteristicAssistance(assistanceCharacteristic = characteristic)
                list.add(newCharacteristic)
            }
        }
    )

    onAssistanceFormChange(newAssistanceForm)
}
