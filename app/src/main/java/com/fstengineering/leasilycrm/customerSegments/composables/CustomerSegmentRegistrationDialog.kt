package com.fstengineering.leasilycrm.customerSegments.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentField
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentFormUiState
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentTypeUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CustomerSegmentRegistrationDialog(
    customerSegmentForm: CustomerSegmentFormUiState,
    onCustomerSegmentFormChange: (CustomerSegmentFormUiState) -> Unit,
    customerSegmentTypes: ImmutableList<CustomerSegmentTypeUiState>,
    formErrors: ImmutableMap<CustomerSegmentField, Int?>,
    isEditing: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(250.milliseconds)
        nameFieldFocusRequester.requestFocus()
    }

    AlertDialog(
        title = {
            Text(
                text = stringResource(
                    if (isEditing) R.string.customer_segments_edit_dialog_headline else R.string.customer_segments_save_dialog_headline
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column {
                AppTextField(
                    placeholder = stringResource(R.string.customer_segments_save_edit_dialog_name_placeholder),
                    value = customerSegmentForm.name ?: "",
                    onValueChange = { nameInput ->
                        onCustomerSegmentFormChange(
                            customerSegmentForm.copy(name = nameInput.capitalizeFirst()),
                        )
                    },
                    isInvalid = formErrors[CustomerSegmentField.Name] != null,
                    supportingText = stringResource(
                        formErrors[CustomerSegmentField.Name] ?: R.string.inline_required,
                    ),
                    maxLength = 75,
                    fieldFocusRequester = nameFieldFocusRequester,
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppRadioGroup(
                    customerSegmentTypes = customerSegmentTypes,
                    selectedType = customerSegmentForm.type,
                    onTypeSelected = { type ->
                        onCustomerSegmentFormChange(
                            customerSegmentForm.copy(type = type),
                        )
                    },
                    isInvalid = formErrors[CustomerSegmentField.Type] != null,
                    supportingText = stringResource(
                        formErrors[CustomerSegmentField.Type] ?: R.string.inline_required,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave() },
            ) {
                Text(
                    text = stringResource(
                        if (isEditing) R.string.edit_action_button_text
                        else R.string.save_action_button_text,
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(
                    text = stringResource(R.string.cancel_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        onDismissRequest = {},
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .width(450.dp)
    )
}

@Composable
fun AppRadioGroup(
    customerSegmentTypes: ImmutableList<CustomerSegmentTypeUiState>,
    selectedType: CustomerSegmentTypeUiState?,
    onTypeSelected: (CustomerSegmentTypeUiState) -> Unit,
    supportingText: String?,
    modifier: Modifier = Modifier,
    isInvalid: Boolean = false,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.customer_segments_save_edit_dialog_type_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(
            modifier = Modifier
                .selectableGroup()
        ) {
            customerSegmentTypes.forEach { type ->
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = type == selectedType,
                            onClick = { onTypeSelected(type) },
                            role = Role.RadioButton,
                        )
                ) {
                    RadioButton(
                        selected = type == selectedType,
                        onClick = null,
                    )
                    Text(
                        text = type.name!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                }
            }
            if (supportingText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isInvalid -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun CustomerSegmentRegistrationDialogPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CustomerSegmentRegistrationDialog(
            customerSegmentForm = CustomerSegmentFormUiState(),
            onCustomerSegmentFormChange = { _ -> },
            customerSegmentTypes = persistentListOf(
                CustomerSegmentTypeUiState(id = 1, name = "Proprietário"),
                CustomerSegmentTypeUiState(id = 2, name = "Inquilino"),
            ),
            formErrors = persistentMapOf(),
            isEditing = false,
            onSave = {},
            onCancel = {},
            modifier = modifier,
        )
    }
}
