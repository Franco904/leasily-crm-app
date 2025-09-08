package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun ConfirmLeaveRegistrationDialog(
    isEditing: Boolean,
    onConfirmLeave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTitleAndMessageDialog(
        title = stringResource(
            when {
                isEditing -> R.string.confirm_leave_registration_dialog_headline_edit
                else -> R.string.confirm_leave_registration_dialog_headline_create
            },
        ),
        message = stringResource(
            when {
                isEditing -> R.string.confirm_leave_registration_dialog_supporting_text_edit
                else -> R.string.confirm_leave_registration_dialog_supporting_text_create
            }
        ),
        confirmButtonText = stringResource(R.string.confirm_action_button_text),
        dismissButtonText = stringResource(R.string.cancel_action_button_text),
        onConfirmButtonClick = onConfirmLeave,
        onDismissButtonClick = onCancel,
        modifier = modifier
    )
}
