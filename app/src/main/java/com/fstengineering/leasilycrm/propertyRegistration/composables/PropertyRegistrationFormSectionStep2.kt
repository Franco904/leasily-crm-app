package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.composables.AppCheckbox
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.BackStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.rememberCenterScroller
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.IncludedItemsFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.PropertyCharacteristicsCountFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.PropertyOtherCharacteristics
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.SeaProximityTypeFormField
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep2Field
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
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
    val outlineColor = MaterialTheme.colorScheme.outline

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

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
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
            PropertyCharacteristicsCountFormField(
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
            PropertyOtherCharacteristics(
                propertyForm = propertyForm,
                onPropertyFormChange = onPropertyFormChange,
                propertyCharacteristics = propertyCharacteristics,
            )
            Spacer(modifier = Modifier.height(120.dp))
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .drawBehind {
                    drawLine(
                        color = outlineColor,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = size.width, y = 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                BackStepButton(onBack = onBack)
                Spacer(modifier = Modifier.width(8.dp))
                NextStepButton(onNext = {
                    if (propertyForm.includedItems.isNullOrBlank()) {
                        onPropertyFormChange(propertyForm.copy(hasIncludedItems = false))
                    }

                    onNext()
                })
            }
        }
    }
}
