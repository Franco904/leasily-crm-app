package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.fields

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicSelectableChip
import com.fstengineering.leasilycrm.core.presentation.utils.presentationLabel
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PreferredOtherCharacteristicsFormField(
    form: AtendimentoDraftFormUiState.Loaded,
    onFormChange: (AtendimentoDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    val otherCharacteristics = remember { ImovelCharacteristic.entries.toImmutableList() }

    FlowRow(
        modifier = modifier
    ) {
        otherCharacteristics.forEach { characteristic ->
            val onSelect = remember(characteristic, form, onFormChange) {
                {
                    onAtendimentoImovelCharacteristicSelect(
                        characteristic = characteristic,
                        form = form,
                        onFormChange = onFormChange,
                    )
                }
            }

            CharacteristicSelectableChip(
                label = stringResource(characteristic.presentationLabel()),
                isSelected = characteristic in form.preferredImovelCharacteristics,
                onSelect = onSelect,
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

private fun onAtendimentoImovelCharacteristicSelect(
    characteristic: ImovelCharacteristic,
    form: AtendimentoDraftFormUiState.Loaded,
    onFormChange: (AtendimentoDraftFormUiState.Loaded) -> Unit,
) {
    val selectedCharacteristics = form.preferredImovelCharacteristics
        .toPersistentList()

    val newForm = form.copy(
        preferredImovelCharacteristics = selectedCharacteristics.mutate { selectedOnes ->
            if (characteristic in selectedOnes) {
                selectedOnes.remove(characteristic)
            } else {
                selectedOnes.add(characteristic)
            }
        }
    )

    onFormChange(newForm)
}
