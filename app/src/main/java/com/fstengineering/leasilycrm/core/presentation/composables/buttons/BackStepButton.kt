package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R

@Composable
fun BackStepButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        onClick = onBack,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.back_action_button_text),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
