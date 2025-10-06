package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun SavePrimaryButton(
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppPrimaryButton(
        label = stringResource(R.string.save_action_button_text),
        onClick = onSave,
        modifier = modifier
    )
}
