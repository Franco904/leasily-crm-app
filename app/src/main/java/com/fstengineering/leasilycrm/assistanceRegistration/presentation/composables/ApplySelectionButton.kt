package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppPrimaryButton

@Composable
fun ApplySelectionButton(
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppPrimaryButton(
        label = stringResource(R.string.apply_action_button_text),
        onClick = onAdd,
        modifier = modifier
    )
}
