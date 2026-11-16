package com.fstengineering.leasilycrm.clienteRegistration.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClienteCityFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClienteContactOriginFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClienteEmailFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClienteIdentityDocumentFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClienteNameFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClienteObservationsFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClientePhoneFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.fields.ClienteTagFormField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteDraftFormUiState
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteField
import com.fstengineering.leasilycrm.core.presentation.composables.rememberVerticalCenterScroller
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow

@Composable
fun ClienteRegistrationFormSection(
    clienteForm: ClienteDraftFormUiState.Loaded,
    onClienteFormChange: (ClienteDraftFormUiState.Loaded) -> Unit,
    pastTags: ImmutableList<String>,
    pastCities: ImmutableList<String>,
    pastContactOrigins: ImmutableList<String>,
    formErrors: ImmutableMap<ClienteField, Int?>,
    onLastTextFieldDone: () -> Unit,
    scrollToFieldFlow: Flow<ClienteField>,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val centerScrollState = rememberVerticalCenterScroller(scrollState)

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var fieldsCoords by remember {
        mutableStateOf<PersistentMap<ClienteField, LayoutCoordinates?>>(
            ClienteField.entries
                .associateWith { null }
                .toPersistentMap(),
        )
    }

    val nameFieldFocusRequester = remember { FocusRequester() }
    val cityFieldFocusRequester = remember { FocusRequester() }
    val identityDocumentFieldFocusRequester = remember { FocusRequester() }
    val phoneFieldFocusRequester = remember { FocusRequester() }
    val emailFieldFocusRequester = remember { FocusRequester() }
    val contactOriginFieldFocusRequester = remember { FocusRequester() }
    val observationsFieldFocusRequester = remember { FocusRequester() }

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
        Spacer(modifier = Modifier.height(28.dp))
        ClienteTagFormField(
            tag = clienteForm.tag,
            pastTags = pastTags,
            onTagChange = { onClienteFormChange(clienteForm.copy(tag = it?.ifBlank { null })) },
            onDone = { nameFieldFocusRequester.requestFocus() },
        )
        Spacer(modifier = Modifier.height(28.dp))
        ClienteNameFormField(
            name = clienteForm.name,
            onNameChange = { onClienteFormChange(clienteForm.copy(name = it?.ifBlank { null })) },
            error = formErrors[ClienteField.Name],
            onDone = { cityFieldFocusRequester.requestFocus() },
            fieldFocusRequester = nameFieldFocusRequester,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    fieldsCoords = fieldsCoords.mutate { it[ClienteField.Name] = coords }
                }
        )
        Spacer(modifier = Modifier.height(28.dp))
        ClienteCityFormField(
            city = clienteForm.city,
            onCityChange = { onClienteFormChange(clienteForm.copy(city = it?.ifBlank { null })) },
            pastCities = pastCities,
            onDone = { identityDocumentFieldFocusRequester.requestFocus() },
            fieldFocusRequester = cityFieldFocusRequester,
        )
        Spacer(modifier = Modifier.height(28.dp))
        ClienteIdentityDocumentFormField(
            identityDocument = clienteForm.identityDocument,
            onIdentityDocumentChange = {
                onClienteFormChange(clienteForm.copy(identityDocument = it?.ifBlank { null }))
            },
            onDone = { phoneFieldFocusRequester.requestFocus() },
            fieldFocusRequester = identityDocumentFieldFocusRequester,
        )
        Spacer(modifier = Modifier.height(28.dp))
        ClientePhoneFormField(
            phone = clienteForm.phone,
            onPhoneChange = { onClienteFormChange(clienteForm.copy(phone = it?.ifBlank { null })) },
            error = formErrors[ClienteField.Phone],
            onDone = { emailFieldFocusRequester.requestFocus() },
            fieldFocusRequester = phoneFieldFocusRequester,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    fieldsCoords = fieldsCoords.mutate { it[ClienteField.Phone] = coords }
                }
        )
        Spacer(modifier = Modifier.height(28.dp))
        ClienteEmailFormField(
            email = clienteForm.email,
            onEmailChange = { onClienteFormChange(clienteForm.copy(email = it?.ifBlank { null })) },
            error = formErrors[ClienteField.Email],
            onDone = { contactOriginFieldFocusRequester.requestFocus() },
            fieldFocusRequester = emailFieldFocusRequester,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    fieldsCoords = fieldsCoords.mutate { it[ClienteField.Email] = coords }
                }
        )
        Spacer(modifier = Modifier.height(28.dp))
        ClienteContactOriginFormField(
            contactOrigin = clienteForm.contactOrigin,
            onContactOriginChange = {
                onClienteFormChange(clienteForm.copy(contactOrigin = it?.ifBlank { null }))
            },
            pastContactOrigins = pastContactOrigins,
            onDone = onLastTextFieldDone,
            fieldFocusRequester = contactOriginFieldFocusRequester,
        )
        Spacer(modifier = Modifier.height(28.dp))
        ClienteObservationsFormField(
            observations = clienteForm.observations,
            onObservationsChange = {
                onClienteFormChange(clienteForm.copy(observations = it?.ifBlank { null }))
            },
            isLead = clienteForm.isLead,
            fieldFocusRequester = observationsFieldFocusRequester,
        )
        Spacer(modifier = Modifier.height(240.dp))
    }
}
