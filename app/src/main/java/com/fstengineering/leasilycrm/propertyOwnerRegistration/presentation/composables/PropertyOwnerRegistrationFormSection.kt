package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.cards.DocumentFileCard
import com.fstengineering.leasilycrm.core.presentation.composables.centerVisibleLazyItemByKey
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerDraftFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerField
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow

@Composable
fun PropertyOwnerRegistrationFormSection(
    propertyOwnerForm: PropertyOwnerDraftFormUiState.Loaded,
    onPropertyOwnerFormChange: (PropertyOwnerDraftFormUiState.Loaded) -> Unit,
    formErrors: ImmutableMap<PropertyOwnerField, Int?>,
    onLastTextFieldDone: () -> Unit,
    onAddPropertyOwnerFile: () -> Unit,
    scrollToFieldFlow: Flow<PropertyOwnerField>,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    val bringIntoViewRequesters = remember {
        PropertyOwnerField.entries.associateWith { BringIntoViewRequester() }
    }

    val nameFieldFocusRequester = remember { FocusRequester() }
    val phoneFieldFocusRequester = remember { FocusRequester() }
    val emailFieldFocusRequester = remember { FocusRequester() }
    val paymentKeyFieldFocusRequester = remember { FocusRequester() }

    val propertyOwnerNotDeletedFiles = remember(propertyOwnerForm) {
        propertyOwnerForm.files.toPersistentList()
    }

    LaunchedEffect(Unit) {
        scrollToFieldFlow.collect { fieldToScroll ->
            bringIntoViewRequesters[fieldToScroll]?.bringIntoView()

            centerVisibleLazyItemByKey(
                lazyListState = lazyListState,
                key = fieldToScroll,
                fallbackIndex = fieldToScroll.scrollIndex,
            )
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 240.dp),
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = PropertyOwnerField.Name) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(PropertyOwnerField.Name))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_name_label),
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
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = PropertyOwnerField.Phone) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(PropertyOwnerField.Phone))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_phone_label),
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
                    keyboardType = KeyboardType.Phone,
                    onDone = { emailFieldFocusRequester.requestFocus() },
                    fieldFocusRequester = phoneFieldFocusRequester,
                )
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = PropertyOwnerField.Email) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(PropertyOwnerField.Email))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_email_label),
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
                    keyboardType = KeyboardType.Email,
                    onDone = { paymentKeyFieldFocusRequester.requestFocus() },
                    fieldFocusRequester = emailFieldFocusRequester,
                )
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = PropertyOwnerField.PaymentKey) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(PropertyOwnerField.PaymentKey))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_payment_key_label),
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
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
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
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Text(
                text = stringResource(R.string.property_owner_registration_documents_body_text),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        items(
            items = propertyOwnerNotDeletedFiles,
            key = { file -> file.id ?: file.name },
        ) { file ->
            DocumentFileCard(
                fileName = file.name,
                actionIcon = Icons.Filled.Close,
                onAction = {
                    val newFiles = propertyOwnerNotDeletedFiles.remove(file)

                    onPropertyOwnerFormChange(
                        propertyOwnerForm.copy(files = newFiles),
                    )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
