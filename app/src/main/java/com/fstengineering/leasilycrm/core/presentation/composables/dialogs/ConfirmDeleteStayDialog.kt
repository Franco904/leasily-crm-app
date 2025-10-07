package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun ConfirmDeleteStayDialog(
    isUnavailablePeriod: Boolean,
    onConfirmDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleId =
        if (isUnavailablePeriod) R.string.stay_unavailable_period_delete_dialog_headline
        else R.string.stay_delete_dialog_headline

    val messageId =
        if (isUnavailablePeriod) R.string.stay_unavailable_period_delete_dialog_supporting_text
        else R.string.stay_delete_dialog_supporting_text

    AppTitleAndMessageDialog(
        title = stringResource(titleId),
        message = stringResource(messageId),
        confirmButtonText = stringResource(id = R.string.delete_action_button_text),
        dismissButtonText = stringResource(id = R.string.cancel_action_button_text),
        onConfirmButtonClick = onConfirmDelete,
        onDismissButtonClick = onCancel,
        modifier = modifier
    )
}
