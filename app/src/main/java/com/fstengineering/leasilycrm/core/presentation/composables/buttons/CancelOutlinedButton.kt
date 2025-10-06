package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun CancelOutlinedButton(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppOutlinedButton(
        label = stringResource(R.string.cancel_action_button_text),
        onClick = onCancel,
        modifier = modifier
    )
}
