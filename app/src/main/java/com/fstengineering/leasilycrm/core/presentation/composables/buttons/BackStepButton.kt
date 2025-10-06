package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun BackStepButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppOutlinedButton(
        label = stringResource(R.string.back_action_button_text),
        onClick = onBack,
        modifier = modifier
    )
}
