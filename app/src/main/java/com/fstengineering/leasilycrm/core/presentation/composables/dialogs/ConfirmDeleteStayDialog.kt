package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun ConfirmDeleteStayDialog(
    onConfirmDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTitleAndMessageDialog(
        title = stringResource(R.string.stay_delete_dialog_headline),
        message = stringResource(R.string.stay_delete_dialog_supporting_text),
        confirmButtonText = stringResource(id = R.string.delete_action_button_text),
        dismissButtonText = stringResource(id = R.string.cancel_action_button_text),
        onConfirmButtonClick = onConfirmDelete,
        onDismissButtonClick = onCancel,
        modifier = modifier
    )
}
