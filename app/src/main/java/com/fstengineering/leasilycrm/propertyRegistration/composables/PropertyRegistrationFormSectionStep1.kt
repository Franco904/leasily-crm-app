package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.APARTMENT_PROPERTY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.rememberVerticalCenterScroller
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.NeighborhoodFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.AddressLine2FormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.CepPostalCodeFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.HobbyBoxNumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.NumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.ParkingBasementLevelNumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.ParkingSpotNumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.PropertyOwnerFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.PropertyTypeFormField
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.StreetAddressFormField
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep1Field
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow

@Composable
fun PropertyRegistrationFormSectionStep1(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    propertyTypes: ImmutableList<PropertyType>,
    pastNeighborhoods: ImmutableList<String>,
    formErrors: ImmutableMap<PropertyStep1Field, Int?>,
    onSelectPropertyOwner: () -> Unit,
    onLastTextFieldDone: () -> Unit,
    scrollToFieldFlow: Flow<PropertyStep1Field>,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    val scrollState = rememberScrollState()
    val centerScrollState = rememberVerticalCenterScroller(scrollState)

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var fieldsCoords by remember {
        mutableStateOf<PersistentMap<PropertyStep1Field, LayoutCoordinates?>>(
            PropertyStep1Field.entries
                .associateWith { null }
                .toPersistentMap(),
        )
    }

    val neighborhoodFieldFocusRequester = remember { FocusRequester() }
    val streetAddressFieldFocusRequester = remember { FocusRequester() }
    val cepPostalCodeFieldFocusRequester = remember { FocusRequester() }
    val numberFieldFocusRequester = remember { FocusRequester() }
    val addressLine2FieldFocusRequester = remember { FocusRequester() }
    val parkingSpotNumberFieldFocusRequester = remember { FocusRequester() }
    val parkingBasementLevelNumberFieldFocusRequester = remember { FocusRequester() }
    val hobbyBoxNumberFieldFocusRequester = remember { FocusRequester() }

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
                text = stringResource(R.string.property_registration_step_1_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(24.dp))
            PropertyOwnerFormField(
                propertyOwner = propertyForm.owner,
                onSelectPropertyOwner = onSelectPropertyOwner,
                error = formErrors[PropertyStep1Field.PropertyOwner],
                modifier = Modifier
                    .onGloballyPositioned { coords ->
                        fieldsCoords = fieldsCoords.mutate { map ->
                            map[PropertyStep1Field.PropertyOwner] = coords
                        }
                    }
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.property_registration_property_type_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PropertyTypeFormField(
                propertyTypes = propertyTypes.toImmutableList(),
                selectedPropertyType = propertyForm.type,
                onPropertyTypeChange = { type ->
                    onPropertyFormChange(
                        propertyForm.copy(
                            type = type,
                            parkingSpotNumber = if (type?.id != APARTMENT_PROPERTY_TYPE_ID) null else propertyForm.parkingSpotNumber,
                            parkingBasementLevelNumber = if (type?.id != APARTMENT_PROPERTY_TYPE_ID) null else propertyForm.parkingBasementLevelNumber,
                            hobbyBoxNumber = if (type?.id != APARTMENT_PROPERTY_TYPE_ID) null else propertyForm.hobbyBoxNumber,
                        ),
                    )
                },
                error = formErrors[PropertyStep1Field.PropertyType],
                modifier = Modifier
                    .onGloballyPositioned { coords ->
                        fieldsCoords = fieldsCoords.mutate { map ->
                            map[PropertyStep1Field.PropertyType] = coords
                        }
                    }
            )
            Spacer(modifier = Modifier.height(24.dp))
            NeighborhoodFormField(
                value = propertyForm.neighborhood,
                onValueChange = { onPropertyFormChange(propertyForm.copy(neighborhood = it)) },
                pastNeighborhoods = pastNeighborhoods,
                error = formErrors[PropertyStep1Field.Neighborhood],
                onDone = {
                    if (propertyForm.streetAddress == null) streetAddressFieldFocusRequester.requestFocus()
                },
                fieldFocusRequester = neighborhoodFieldFocusRequester,
                modifier = Modifier
                    .onGloballyPositioned { coords ->
                        fieldsCoords = fieldsCoords.mutate { map ->
                            map[PropertyStep1Field.Neighborhood] = coords
                        }
                    },
            )
            Spacer(modifier = Modifier.height(28.dp))
            StreetAddressFormField(
                value = propertyForm.streetAddress,
                onValueChange = { onPropertyFormChange(propertyForm.copy(streetAddress = it)) },
                error = formErrors[PropertyStep1Field.StreetAddress],
                onDone = { cepPostalCodeFieldFocusRequester.requestFocus() },
                fieldFocusRequester = streetAddressFieldFocusRequester,
                modifier = Modifier
                    .onGloballyPositioned { coords ->
                        fieldsCoords = fieldsCoords.mutate { map ->
                            map[PropertyStep1Field.StreetAddress] = coords
                        }
                    }
            )
            Spacer(modifier = Modifier.height(28.dp))
            CepPostalCodeFormField(
                value = propertyForm.cepPostalCode,
                onValueChange = { onPropertyFormChange(propertyForm.copy(cepPostalCode = it)) },
                onDone = {
                    if (propertyForm.number == null) numberFieldFocusRequester.requestFocus()
                },
                fieldFocusRequester = cepPostalCodeFieldFocusRequester,
            )
            Spacer(modifier = Modifier.height(28.dp))
            NumberFormField(
                value = if (propertyForm.number == null) "" else propertyForm.number.toString(),
                onValueChange = { onPropertyFormChange(propertyForm.copy(number = it.toIntOrNull())) },
                onDone = {
                    if (propertyForm.addressLine2 == null) addressLine2FieldFocusRequester.requestFocus()
                },
                fieldFocusRequester = numberFieldFocusRequester,
            )
            Spacer(modifier = Modifier.height(28.dp))
            AddressLine2FormField(
                value = propertyForm.addressLine2,
                onValueChange = { onPropertyFormChange(propertyForm.copy(addressLine2 = it)) },
                onDone = {
                    if (propertyForm.type?.id == APARTMENT_PROPERTY_TYPE_ID && propertyForm.parkingSpotNumber == null) {
                        parkingSpotNumberFieldFocusRequester.requestFocus()
                    } else {
                        onLastTextFieldDone()
                    }
                },
                fieldFocusRequester = addressLine2FieldFocusRequester,
            )
            if (propertyForm.type?.id == APARTMENT_PROPERTY_TYPE_ID) {
                Column {
                    Spacer(modifier = Modifier.height(28.dp))
                    ParkingSpotNumberFormField(
                        value = propertyForm.parkingSpotNumber,
                        onValueChange = {
                            onPropertyFormChange(propertyForm.copy(parkingSpotNumber = it))
                        },
                        onDone = {
                            if (propertyForm.parkingBasementLevelNumber == null)
                                parkingBasementLevelNumberFieldFocusRequester.requestFocus()
                        },
                        fieldFocusRequester = parkingSpotNumberFieldFocusRequester,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    ParkingBasementLevelNumberFormField(
                        value = propertyForm.parkingBasementLevelNumber ?: "",
                        onValueChange = {
                            onPropertyFormChange(propertyForm.copy(parkingBasementLevelNumber = it))
                        },
                        onDone = {
                            if (propertyForm.hobbyBoxNumber == null)
                                hobbyBoxNumberFieldFocusRequester.requestFocus()
                        },
                        fieldFocusRequester = parkingBasementLevelNumberFieldFocusRequester,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    HobbyBoxNumberFormField(
                        value = propertyForm.hobbyBoxNumber,
                        onValueChange = { onPropertyFormChange(propertyForm.copy(hobbyBoxNumber = it)) },
                        onDone = { onLastTextFieldDone() },
                        fieldFocusRequester = hobbyBoxNumberFieldFocusRequester,
                    )
                }
            }
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
                NextStepButton(onNext = onNext)
            }
        }
    }
}
