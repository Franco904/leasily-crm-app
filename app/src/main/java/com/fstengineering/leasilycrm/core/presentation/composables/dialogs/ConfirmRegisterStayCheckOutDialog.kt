package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun ConfirmRegisterStayCheckOutDialog(
    onConfirmCheckOut: (Boolean) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mustNotShowCheckOutDialogAnymore by rememberSaveable { mutableStateOf(false) }

    AppTitleAndMessageDialog(
        title = stringResource(R.string.stay_register_check_out_dialog_headline),
        message = stringResource(R.string.stay_register_check_out_dialog_supporting_text),
        confirmButtonText = stringResource(id = R.string.register_button_text),
        dismissButtonText = stringResource(id = R.string.cancel_action_button_text),
        onConfirmButtonClick = { onConfirmCheckOut(mustNotShowCheckOutDialogAnymore) },
        onDismissButtonClick = onCancel,
        hasDontShowAnymoreCheckBox = true,
        isDontShowAnymoreChecked = mustNotShowCheckOutDialogAnymore,
        onDontShowDialogAnymore = { mustNotShowCheckOutDialogAnymore = true },
        modifier = modifier
    )
}
