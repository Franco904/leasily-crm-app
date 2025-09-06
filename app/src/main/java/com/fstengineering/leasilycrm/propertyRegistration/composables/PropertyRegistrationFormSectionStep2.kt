package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristicProperty
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.composables.AppCheckbox
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicChip
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.BackStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.rememberCenterScroller
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.QuantityCharacteristicsField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.SeaProximityTypeFormField
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep2Field
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow

@Composable
fun PropertyRegistrationFormSectionStep2(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    seaProximityTypes: ImmutableList<PropertySeaProximityType>,
    propertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    formErrors: ImmutableMap<PropertyStep2Field, Int?>,
    scrollToFieldFlow: Flow<PropertyStep2Field>,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val centerScrollState = rememberCenterScroller(scrollState)

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var fieldsCoords by remember {
        mutableStateOf<PersistentMap<PropertyStep2Field, LayoutCoordinates?>>(
            PropertyStep2Field.entries
                .associateWith { null }
                .toPersistentMap(),
        )
    }

    LaunchedEffect(Unit) {
        scrollToFieldFlow.collect { fieldToScroll ->
            val fieldCoords = fieldsCoords[fieldToScroll]
            centerScrollState(scrollContainerCoords, fieldCoords)
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .onGloballyPositioned { coords -> scrollContainerCoords = coords }
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.property_registration_step_2_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuantityCharacteristicsField(
            propertyForm = propertyForm,
            onPropertyFormChange = onPropertyFormChange,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.property_registration_sea_proximity_type_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SeaProximityTypeFormField(
            propertyForm = propertyForm,
            onPropertyFormChange = onPropertyFormChange,
            onDistanceToSeaPosition = { coords ->
                fieldsCoords = fieldsCoords.mutate { map ->
                    map[PropertyStep2Field.DistanceToSea] = coords
                }
            },
            distanceToSeaError = formErrors[PropertyStep2Field.DistanceToSea],
            seaProximityTypes = seaProximityTypes,
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        IncludedItemsFormField(
            value = propertyForm.includedItems,
            onValueChange = {
                onPropertyFormChange(
                    propertyForm.copy(
                        includedItems = it,
                        hasIncludedItems = true,
                    )
                )
            },
        )
        AppCheckbox(
            label = stringResource(R.string.property_registration_does_not_include_items_body_text),
            isChecked = !(propertyForm.hasIncludedItems ?: true),
            onCheckedChange = { newNotHasItems ->
                onPropertyFormChange(propertyForm.copy(hasIncludedItems = !newNotHasItems))
            },
            isEnabled = propertyForm.includedItems.isNullOrBlank(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.property_registration_other_characteristics_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow {
            propertyCharacteristics.forEach { characteristic ->
                CharacteristicChip(
                    label = characteristic.name,
                    isSelected = characteristic in propertyForm.characteristics.map {
                        it.propertyCharacteristic
                    },
                    onSelect = {
                        onSelectPropertyCharacteristic(
                            characteristic = characteristic,
                            propertyForm = propertyForm,
                            onPropertyFormChange = onPropertyFormChange,
                        )
                    },
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.End)
        ) {
            BackStepButton(onBack = onBack)
            Spacer(modifier = Modifier.width(8.dp))
            NextStepButton(onNext = {
                if (propertyForm.includedItems.isNullOrBlank()) {
                    onPropertyFormChange(propertyForm.copy(hasIncludedItems = false))
                }

                onNext()
            })
        }
        Spacer(modifier = Modifier.height(120.dp))
    }
}

private fun onSelectPropertyCharacteristic(
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

                if (associatedCharacteristic.propertyId != null) {
                    val i = list.indexOf(associatedCharacteristic)
                    list[i] = associatedCharacteristic.copy(isDeleted = true)
                } else {
                    list.remove(associatedCharacteristic)
                }
            } else {
                val newCharacteristic =
                    PropertyCharacteristicProperty(propertyCharacteristic = characteristic)
                list.add(newCharacteristic)
            }
        }
    )

    onPropertyFormChange(newPropertyForm)
}
