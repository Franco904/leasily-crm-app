package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables

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
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.rememberVerticalCenterScroller
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.NeighborhoodFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.AddressLine2FormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.CepPostalCodeFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.HobbyBoxNumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.NumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.OwnerFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.ParkingBasementLevelNumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.ParkingSpotNumberFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.StreetAddressFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.TypeFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyStep1Field
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow

@Composable
fun PropertyRegistrationFormSectionStep1(
    form: PropertyDraftFormUiState.Loaded,
    onFormChange: (PropertyDraftFormUiState.Loaded) -> Unit,
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
            OwnerFormField(
                propertyOwner = form.owner,
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
            TypeFormField(
                selectedPropertyType = form.type,
                onPropertyTypeChange = { type ->
                    onFormChange(
                        form.copy(
                            type = type,
                            parkingSpotNumber = if (type?.id != APARTMENT_PROPERTY_TYPE_ID) null else form.parkingSpotNumber,
                            parkingBasementLevelNumber = if (type?.id != APARTMENT_PROPERTY_TYPE_ID) null else form.parkingBasementLevelNumber,
                            hobbyBoxNumber = if (type?.id != APARTMENT_PROPERTY_TYPE_ID) null else form.hobbyBoxNumber,
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
                value = form.neighborhood,
                onValueChange = { onFormChange(form.copy(neighborhood = it)) },
                pastNeighborhoods = pastNeighborhoods,
                error = formErrors[PropertyStep1Field.Neighborhood],
                onDone = {
                    if (form.streetAddress == null) streetAddressFieldFocusRequester.requestFocus()
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
                value = form.streetAddress,
                onValueChange = { onFormChange(form.copy(streetAddress = it)) },
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
                value = form.cepPostalCode,
                onValueChange = { onFormChange(form.copy(cepPostalCode = it)) },
                onDone = {
                    if (form.number == null) numberFieldFocusRequester.requestFocus()
                },
                fieldFocusRequester = cepPostalCodeFieldFocusRequester,
            )
            Spacer(modifier = Modifier.height(28.dp))
            NumberFormField(
                value = if (form.number == null) "" else form.number.toString(),
                onValueChange = { onFormChange(form.copy(number = it.toIntOrNull())) },
                onDone = {
                    if (form.addressLine2 == null) addressLine2FieldFocusRequester.requestFocus()
                },
                fieldFocusRequester = numberFieldFocusRequester,
            )
            Spacer(modifier = Modifier.height(28.dp))
            AddressLine2FormField(
                value = form.addressLine2,
                onValueChange = { onFormChange(form.copy(addressLine2 = it)) },
                onDone = {
                    if (form.type?.id == APARTMENT_PROPERTY_TYPE_ID && form.parkingSpotNumber == null) {
                        parkingSpotNumberFieldFocusRequester.requestFocus()
                    } else {
                        onLastTextFieldDone()
                    }
                },
                fieldFocusRequester = addressLine2FieldFocusRequester,
            )
            if (form.type?.id == APARTMENT_PROPERTY_TYPE_ID) {
                Column {
                    Spacer(modifier = Modifier.height(28.dp))
                    ParkingSpotNumberFormField(
                        value = form.parkingSpotNumber,
                        onValueChange = {
                            onFormChange(form.copy(parkingSpotNumber = it))
                        },
                        onDone = {
                            if (form.parkingBasementLevelNumber == null)
                                parkingBasementLevelNumberFieldFocusRequester.requestFocus()
                        },
                        fieldFocusRequester = parkingSpotNumberFieldFocusRequester,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    ParkingBasementLevelNumberFormField(
                        value = form.parkingBasementLevelNumber ?: "",
                        onValueChange = {
                            onFormChange(form.copy(parkingBasementLevelNumber = it))
                        },
                        onDone = {
                            if (form.hobbyBoxNumber == null)
                                hobbyBoxNumberFieldFocusRequester.requestFocus()
                        },
                        fieldFocusRequester = parkingBasementLevelNumberFieldFocusRequester,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    HobbyBoxNumberFormField(
                        value = form.hobbyBoxNumber,
                        onValueChange = { onFormChange(form.copy(hobbyBoxNumber = it)) },
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
