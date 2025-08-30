package com.fstengineering.leasilycrm.propertyOwnerRegistration.composables

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import com.fstengineering.leasilycrm.core.presentation.composables.rememberCenterScroller
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.propertyOwnerRegistration.models.PropertyOwnerField
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow

@Composable
fun PropertyOwnerRegistrationFormSection(
    propertyOwnerForm: PropertyOwner,
    onPropertyOwnerFormChange: (PropertyOwner) -> Unit,
    formErrors: ImmutableMap<PropertyOwnerField, Int?>,
    onCustomerSegmentFieldTap: () -> Unit,
    onLastTextFieldDone: () -> Unit,
    onAddPropertyOwnerFile: () -> Unit,
    scrollState: ScrollState,
    scrollToFieldFlow: Flow<PropertyOwnerField>,
    modifier: Modifier = Modifier,
) {
    val centerScrollState = rememberCenterScroller(scrollState)

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var fieldsCoords by remember {
        mutableStateOf<PersistentMap<PropertyOwnerField, LayoutCoordinates?>>(
            PropertyOwnerField.entries
                .associateWith { null }
                .toPersistentMap(),
        )
    }

    val nameFieldFocusRequester = remember { FocusRequester() }
    val phoneFieldFocusRequester = remember { FocusRequester() }
    val emailFieldFocusRequester = remember { FocusRequester() }
    val paymentKeyFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        scrollToFieldFlow.collect { fieldToScroll ->
            val fieldCoords = fieldsCoords[fieldToScroll]
            centerScrollState(scrollContainerCoords, fieldCoords)
        }
    }

    Column(
        modifier = modifier
            .onGloballyPositioned { coords -> scrollContainerCoords = coords }
            .verticalScroll(scrollState)
    ) {
        AppTextField(
            label = stringResource(R.string.property_owner_registration_customer_segment_placeholder),
            value = propertyOwnerForm.customerSegment?.name ?: "",
            onTap = onCustomerSegmentFieldTap,
            suffixIcon = Icons.Default.ArrowDropDown,
            isReadOnly = true,
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            label = stringResource(R.string.property_owner_registration_name_placeholder),
            value = propertyOwnerForm.name ?: "",
            onValueChange = { nameInput ->
                onPropertyOwnerFormChange(
                    propertyOwnerForm.copy(name = nameInput.capitalizeFirst()),
                )
            },
            isInvalid = formErrors[PropertyOwnerField.Name] != null,
            supportingText = stringResource(
                formErrors[PropertyOwnerField.Name] ?: R.string.inline_required,
            ),
            maxLength = 100,
            onDone = { phoneFieldFocusRequester.requestFocus() },
            fieldFocusRequester = nameFieldFocusRequester,
            modifier = Modifier.onGloballyPositioned { coords ->
                fieldsCoords = fieldsCoords.mutate { map ->
                    map[PropertyOwnerField.Name] = coords
                }
            }
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            label = stringResource(R.string.property_owner_registration_phone_placeholder),
            value = propertyOwnerForm.phone ?: "",
            onValueChange = { phoneInput ->
                onPropertyOwnerFormChange(
                    propertyOwnerForm.copy(phone = phoneInput),
                )
            },
            isInvalid = formErrors[PropertyOwnerField.Phone] != null,
            supportingText = run {
                val phoneError = formErrors[PropertyOwnerField.Phone]
                if (phoneError != null) stringResource(phoneError) else null
            },
            maxLength = 20,
            onDone = { emailFieldFocusRequester.requestFocus() },
            fieldFocusRequester = phoneFieldFocusRequester,
            modifier = Modifier.onGloballyPositioned { coords ->
                fieldsCoords = fieldsCoords.mutate { map ->
                    map[PropertyOwnerField.Phone] = coords
                }
            }
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            label = stringResource(R.string.property_owner_registration_email_placeholder),
            value = propertyOwnerForm.email ?: "",
            onValueChange = { emailInput ->
                onPropertyOwnerFormChange(
                    propertyOwnerForm.copy(email = emailInput),
                )
            },
            isInvalid = formErrors[PropertyOwnerField.Email] != null,
            supportingText = run {
                val emailError = formErrors[PropertyOwnerField.Email]
                if (emailError != null) stringResource(emailError) else null
            },
            maxLength = 120,
            onDone = { paymentKeyFieldFocusRequester.requestFocus() },
            fieldFocusRequester = emailFieldFocusRequester,
            modifier = Modifier.onGloballyPositioned { coords ->
                fieldsCoords = fieldsCoords.mutate { map ->
                    map[PropertyOwnerField.Email] = coords
                }
            }
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            label = stringResource(R.string.property_owner_registration_payment_key_placeholder),
            value = propertyOwnerForm.paymentKey ?: "",
            onValueChange = { paymentKeyInput ->
                onPropertyOwnerFormChange(
                    propertyOwnerForm.copy(paymentKey = paymentKeyInput),
                )
            },
            isInvalid = formErrors[PropertyOwnerField.PaymentKey] != null,
            supportingText = run {
                val emailError = formErrors[PropertyOwnerField.PaymentKey]
                if (emailError != null) stringResource(emailError) else null
            },
            maxLength = 120,
            onDone = { onLastTextFieldDone() },
            fieldFocusRequester = paymentKeyFieldFocusRequester,
            modifier = Modifier.onGloballyPositioned { coords ->
                fieldsCoords = fieldsCoords.mutate { map ->
                    map[PropertyOwnerField.PaymentKey] = coords
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.property_owner_registration_documents_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            TextButton(onClick = onAddPropertyOwnerFile) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Text(
            text = stringResource(R.string.property_owner_registration_documents_body_text),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
//        LazyColumn {
//            itemsIndexed(
//                items = propertyOwnerForm.files,
//                key = { i, file -> file.id ?: i },
//                contentType = { _, entryType -> entryType },
//            ) { _, file ->
//
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
        Spacer(modifier = Modifier.height(240.dp))
    }
}
