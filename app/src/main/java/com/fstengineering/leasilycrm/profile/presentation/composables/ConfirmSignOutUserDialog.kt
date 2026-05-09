package com.fstengineering.leasilycrm.profile.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.AppTitleAndMessageDialog

@Composable
fun ConfirmSignOutUserDialog(
    onConfirmSignOut: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTitleAndMessageDialog(
        title = stringResource(R.string.profile_sign_out_dialog_headline),
        message = stringResource(R.string.profile_sign_out_dialog_supporting_text),
        confirmButtonText = stringResource(R.string.sign_out_action_button_text),
        dismissButtonText = stringResource(R.string.cancel_action_button_text),
        onConfirmButtonClick = onConfirmSignOut,
        onDismissButtonClick = onCancel,
        modifier = modifier
    )
}
