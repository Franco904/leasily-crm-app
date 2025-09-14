package com.fstengineering.leasilycrm.leadTenantRegistration.composables

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
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.presentation.composables.rememberCenterScroller
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantCityFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantContactOriginTypeFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantEmailFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantIdentityDocumentFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantNameFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantObservationsFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantPhoneFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields.LeadTenantSegmentFormField
import com.fstengineering.leasilycrm.leadTenantRegistration.models.LeadTenantField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow

@Composable
fun LeadTenantRegistrationFormSection(
    leadTenantForm: Tenant,
    onLeadTenantFormChange: (Tenant) -> Unit,
    pastCities: ImmutableList<String>,
    formErrors: ImmutableMap<LeadTenantField, Int?>,
    onCustomerSegmentFieldTap: () -> Unit,
    onTenantContactOriginFieldTap: () -> Unit,
    onLastTextFieldDone: () -> Unit,
    scrollToFieldFlow: Flow<LeadTenantField>,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val centerScrollState = rememberCenterScroller(scrollState)

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var fieldsCoords by remember {
        mutableStateOf<PersistentMap<LeadTenantField, LayoutCoordinates?>>(
            LeadTenantField.entries
                .associateWith { null }
                .toPersistentMap(),
        )
    }

    val nameFieldFocusRequester = remember { FocusRequester() }
    val cityFieldFocusRequester = remember { FocusRequester() }
    val identityDocumentFieldFocusRequester = remember { FocusRequester() }
    val phoneFieldFocusRequester = remember { FocusRequester() }
    val emailFieldFocusRequester = remember { FocusRequester() }
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
        LeadTenantSegmentFormField(
            tenantSegmentName = leadTenantForm.customerSegment?.name,
            onCustomerSegmentFieldTap = onCustomerSegmentFieldTap,
        )
        Spacer(modifier = Modifier.height(28.dp))
        LeadTenantNameFormField(
            tenantName = leadTenantForm.name,
            onTenantNameChange = { onLeadTenantFormChange(leadTenantForm.copy(name = it)) },
            error = formErrors[LeadTenantField.Name],
            onDone = { cityFieldFocusRequester.requestFocus() },
            fieldFocusRequester = nameFieldFocusRequester,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    fieldsCoords = fieldsCoords.mutate { it[LeadTenantField.Name] = coords }
                }
        )
        Spacer(modifier = Modifier.height(28.dp))
        LeadTenantCityFormField(
            city = leadTenantForm.city,
            onCityChange = { onLeadTenantFormChange(leadTenantForm.copy(city = it)) },
            pastCities = pastCities,
            onDone = { identityDocumentFieldFocusRequester.requestFocus() },
            fieldFocusRequester = cityFieldFocusRequester,
        )
        Spacer(modifier = Modifier.height(28.dp))
        LeadTenantIdentityDocumentFormField(
            identityDocument = leadTenantForm.identityDocument,
            onIdentityDocumentChange = {
                onLeadTenantFormChange(leadTenantForm.copy(identityDocument = it))
            },
            onDone = { phoneFieldFocusRequester.requestFocus() },
            fieldFocusRequester = identityDocumentFieldFocusRequester,
        )
        Spacer(modifier = Modifier.height(28.dp))
        LeadTenantPhoneFormField(
            phone = leadTenantForm.phone,
            onPhoneChange = { onLeadTenantFormChange(leadTenantForm.copy(phone = it)) },
            error = formErrors[LeadTenantField.Phone],
            onDone = { emailFieldFocusRequester.requestFocus() },
            fieldFocusRequester = phoneFieldFocusRequester,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    fieldsCoords = fieldsCoords.mutate { it[LeadTenantField.Phone] = coords }
                }
        )
        Spacer(modifier = Modifier.height(28.dp))
        LeadTenantEmailFormField(
            email = leadTenantForm.email,
            onEmailChange = { onLeadTenantFormChange(leadTenantForm.copy(email = it)) },
            error = formErrors[LeadTenantField.Email],
            onDone = onLastTextFieldDone,
            fieldFocusRequester = emailFieldFocusRequester,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    fieldsCoords = fieldsCoords.mutate { it[LeadTenantField.Email] = coords }
                }
        )
        Spacer(modifier = Modifier.height(28.dp))
        LeadTenantContactOriginTypeFormField(
            tenantContactOriginTypeName = leadTenantForm.tenantContactOriginType?.name,
            onTenantContactOriginTypeFieldTap = onTenantContactOriginFieldTap,
        )
        Spacer(modifier = Modifier.height(28.dp))
        LeadTenantObservationsFormField(
            observations = leadTenantForm.observations,
            onObservationsChange = {
                onLeadTenantFormChange(leadTenantForm.copy(observations = it))
            },
            onDone = onLastTextFieldDone,
            isLead = leadTenantForm.isLead,
            fieldFocusRequester = observationsFieldFocusRequester,
        )
        Spacer(modifier = Modifier.height(240.dp))
    }
}
