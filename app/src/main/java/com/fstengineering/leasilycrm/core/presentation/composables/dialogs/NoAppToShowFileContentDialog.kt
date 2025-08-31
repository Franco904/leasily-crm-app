package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun NoAppToShowFileContentDialog(
    onOpenPlayStore: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTitleAndMessageDialog(
        title = stringResource(R.string.no_app_to_show_file_content_dialog_headline),
        message = stringResource(R.string.no_app_to_show_file_content_dialog_supporting_text),
        confirmButtonText = stringResource(R.string.search_action_button_text),
        dismissButtonText = stringResource(R.string.close_action_button_text),
        onConfirmButtonClick = onOpenPlayStore,
        onDismissButtonClick = onClose,
        modifier = modifier
    )
}
