package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.composables

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
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioField
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow

@Composable
fun ProprietarioRegistrationFormSection(
    proprietarioForm: ProprietarioDraftFormUiState.Loaded,
    onProprietarioFormChange: (ProprietarioDraftFormUiState.Loaded) -> Unit,
    formErrors: ImmutableMap<ProprietarioField, Int?>,
    onLastTextFieldDone: () -> Unit,
    onAddProprietarioFile: () -> Unit,
    scrollToFieldFlow: Flow<ProprietarioField>,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    val bringIntoViewRequesters = remember {
        ProprietarioField.entries.associateWith { BringIntoViewRequester() }
    }

    val nameFieldFocusRequester = remember { FocusRequester() }
    val phoneFieldFocusRequester = remember { FocusRequester() }
    val emailFieldFocusRequester = remember { FocusRequester() }
    val paymentKeyFieldFocusRequester = remember { FocusRequester() }

    val proprietarioNotDeletedFiles = remember(proprietarioForm) {
        proprietarioForm.files.toPersistentList()
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
        item(key = ProprietarioField.Name) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(ProprietarioField.Name))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_name_label),
                    value = proprietarioForm.name ?: "",
                    onValueChange = { nameInput ->
                        onProprietarioFormChange(
                            proprietarioForm.copy(name = nameInput.ifBlank { null }?.capitalizeFirst()),
                        )
                    },
                    isInvalid = formErrors[ProprietarioField.Name] != null,
                    supportingText = stringResource(
                        formErrors[ProprietarioField.Name] ?: R.string.inline_required,
                    ),
                    maxLength = 100,
                    onDone = { phoneFieldFocusRequester.requestFocus() },
                    fieldFocusRequester = nameFieldFocusRequester,
                )
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = ProprietarioField.Phone) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(ProprietarioField.Phone))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_phone_label),
                    value = proprietarioForm.phone ?: "",
                    onValueChange = { phoneInput ->
                        onProprietarioFormChange(
                            proprietarioForm.copy(phone = phoneInput.ifBlank { null }),
                        )
                    },
                    isInvalid = formErrors[ProprietarioField.Phone] != null,
                    supportingText = run {
                        val phoneError = formErrors[ProprietarioField.Phone]
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
        item(key = ProprietarioField.Email) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(ProprietarioField.Email))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_email_label),
                    value = proprietarioForm.email ?: "",
                    onValueChange = { emailInput ->
                        onProprietarioFormChange(
                            proprietarioForm.copy(email = emailInput.ifBlank { null }),
                        )
                    },
                    isInvalid = formErrors[ProprietarioField.Email] != null,
                    supportingText = run {
                        val emailError = formErrors[ProprietarioField.Email]
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
        item(key = ProprietarioField.PaymentKey) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(ProprietarioField.PaymentKey))
            ) {
                FormTextField(
                    label = stringResource(R.string.property_owner_registration_payment_key_label),
                    value = proprietarioForm.paymentKey ?: "",
                    onValueChange = { paymentKeyInput ->
                        onProprietarioFormChange(
                            proprietarioForm.copy(paymentKey = paymentKeyInput.ifBlank { null }),
                        )
                    },
                    isInvalid = formErrors[ProprietarioField.PaymentKey] != null,
                    supportingText = run {
                        val emailError = formErrors[ProprietarioField.PaymentKey]
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
                TextButton(onClick = onAddProprietarioFile) {
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
            items = proprietarioNotDeletedFiles,
            key = { file -> file.id ?: file.name },
        ) { file ->
            DocumentFileCard(
                fileName = file.name,
                actionIcon = Icons.Filled.Close,
                onAction = {
                    val newFiles = proprietarioNotDeletedFiles.remove(file)

                    onProprietarioFormChange(
                        proprietarioForm.copy(files = newFiles),
                    )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
