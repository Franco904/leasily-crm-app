package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun SavePrimaryButton(
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedButton(
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        onClick = onSave,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.save_action_button_text),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
