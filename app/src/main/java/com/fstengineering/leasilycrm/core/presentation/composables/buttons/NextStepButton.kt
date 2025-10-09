package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun NextStepButton(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppPrimaryButton(
        label = stringResource(R.string.advance_action_button_text),
        suffixIcon = Icons.AutoMirrored.Outlined.ArrowForward,
        onClick = onNext,
        modifier = modifier
    )
}
