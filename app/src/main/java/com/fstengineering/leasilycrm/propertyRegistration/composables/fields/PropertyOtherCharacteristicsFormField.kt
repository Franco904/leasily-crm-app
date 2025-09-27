package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristicProperty
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicSelectableChip
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PropertyOtherCharacteristicsFormField(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    propertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    modifier: Modifier = Modifier,
) {
    val notDeletedPropertyCharacteristics = remember(propertyForm.characteristics) {
        propertyForm.characteristics
            .filter { !it.isDeleted }
            .map { it.propertyCharacteristic }
    }

    FlowRow(
        modifier = modifier
    ) {
        propertyCharacteristics.forEach { characteristic ->
            CharacteristicSelectableChip(
                label = characteristic.name,
                isSelected = characteristic in notDeletedPropertyCharacteristics,
                onSelect = {
                    onPropertyCharacteristicSelect(
                        characteristic = characteristic,
                        propertyForm = propertyForm,
                        onPropertyFormChange = onPropertyFormChange,
                    )
                },
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

private fun onPropertyCharacteristicSelect(
    characteristic: PropertyCharacteristic,
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
) {
    val selectedCharacteristics = propertyForm.characteristics
        .toPersistentList()

    val newPropertyForm = propertyForm.copy(
        characteristics = selectedCharacteristics.mutate { list ->
            val characteristics = list.map { it.propertyCharacteristic }

            if (characteristic in characteristics) {
                val associatedCharacteristic =
                    list.find { it.propertyCharacteristic == characteristic } ?: return

                if (associatedCharacteristic.propertyId == null) {
                    list.remove(associatedCharacteristic)
                    return@mutate
                }

                val i = list.indexOf(associatedCharacteristic)
                list[i] = associatedCharacteristic.copy(
                    isDeleted = !associatedCharacteristic.isDeleted,
                )
            } else {
                val newCharacteristic =
                    PropertyCharacteristicProperty(propertyCharacteristic = characteristic)
                list.add(newCharacteristic)
            }
        }
    )

    onPropertyFormChange(newPropertyForm)
}
