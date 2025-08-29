package com.fstengineering.leasilycrm.propertyOwnerRegistration.composables

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.customerSegments.composables.CustomerSegmentCard
import com.fstengineering.leasilycrm.propertyOwnerRegistration.models.PropertyOwnerField
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PropertyOwnerRegistrationFormSection(
    scrollState: ScrollState,
    propertyOwnerForm: PropertyOwner,
    onPropertyOwnerFormChange: (PropertyOwner) -> Unit,
    formErrors: ImmutableMap<PropertyOwnerField, Int?>,
    onCustomerSegmentFieldTap: () -> Unit,
    onAddPropertyOwnerFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameFieldFocusRequester = remember { FocusRequester() }
    val phoneFieldFocusRequester = remember { FocusRequester() }
    val emailFieldFocusRequester = remember { FocusRequester() }
    val paymentKeyFieldFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        AppTextField(
            placeholder = stringResource(R.string.property_owner_registration_customer_segment_placeholder),
            value = propertyOwnerForm.customerSegment?.name ?: "",
            onValueChange = {},
            suffixIcon = Icons.Default.ArrowDropDown,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { onCustomerSegmentFieldTap() }
                }
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            placeholder = stringResource(R.string.property_owner_registration_name_placeholder),
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
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            placeholder = stringResource(R.string.property_owner_registration_phone_placeholder),
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
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            placeholder = stringResource(R.string.property_owner_registration_email_placeholder),
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
        )
        Spacer(modifier = Modifier.height(28.dp))
        AppTextField(
            placeholder = stringResource(R.string.property_owner_registration_payment_key_placeholder),
            value = propertyOwnerForm.email ?: "",
            onValueChange = { paymentKeyInput ->
                onPropertyOwnerFormChange(
                    propertyOwnerForm.copy(email = paymentKeyInput),
                )
            },
            isInvalid = formErrors[PropertyOwnerField.PaymentKey] != null,
            supportingText = run {
                val emailError = formErrors[PropertyOwnerField.PaymentKey]
                if (emailError != null) stringResource(emailError) else null
            },
            maxLength = 120,
            fieldFocusRequester = paymentKeyFieldFocusRequester,
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
    }
}
