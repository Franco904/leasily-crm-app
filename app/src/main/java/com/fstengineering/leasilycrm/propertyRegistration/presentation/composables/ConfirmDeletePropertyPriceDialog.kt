package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.AppTitleAndMessageDialog

@Composable
fun ConfirmDeletePropertyPriceDialog(
    onConfirmDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTitleAndMessageDialog(
        title = stringResource(R.string.property_registration_price_delete_dialog_headline),
        message = stringResource(R.string.property_registration_price_delete_dialog_supporting_text),
        confirmButtonText = stringResource(R.string.delete_action_button_text),
        dismissButtonText = stringResource(R.string.cancel_action_button_text),
        onConfirmButtonClick = onConfirmDelete,
        onDismissButtonClick = onCancel,
        modifier = modifier
    )
}
