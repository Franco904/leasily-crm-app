package com.fstengineering.leasilycrm.customerSegments.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioGroup
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioOption
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CustomerSegmentRegistrationDialog(
    customerSegmentForm: CustomerSegment,
    onCustomerSegmentFormChange: (CustomerSegment) -> Unit,
    customerSegmentTypes: ImmutableList<CustomerSegmentType>,
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
                    label = stringResource(R.string.customer_segments_save_edit_dialog_name_placeholder),
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
                    groupTitle = stringResource(R.string.customer_segments_save_edit_dialog_type_title),
                    radioOptions = customerSegmentTypes.map {
                        AppRadioOption(label = it.name ?: "")
                    }.toImmutableList(),
                    selectedOptionLabel = customerSegmentForm.type?.name,
                    onOptionSelected = { typeName ->
                        val type = customerSegmentTypes.find { it.name == typeName }

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

@Preview
@Composable
fun CustomerSegmentRegistrationDialogPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        CustomerSegmentRegistrationDialog(
            customerSegmentForm = CustomerSegment(),
            onCustomerSegmentFormChange = { _ -> },
            customerSegmentTypes = persistentListOf(
                CustomerSegmentType(id = 1, name = "Proprietário"),
                CustomerSegmentType(id = 2, name = "Inquilino"),
            ),
            formErrors = persistentMapOf(),
            isEditing = false,
            onSave = {},
            onCancel = {},
            modifier = modifier,
        )
    }
}
